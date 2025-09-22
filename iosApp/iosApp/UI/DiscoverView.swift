import UIKit
import SwiftUI
import Shared

@MainActor
class DiscoverViewModel: ObservableObject {
    private let dataProvider: DiscoverUiDataProvider
    @Published public var uiState: DiscoverUiState = DiscoverUiState.companion.Empty
    
    private var sideEffectTask: Task<Void, Never>? = nil
    private var refreshCompleter: OneShotCompleter<Void>? = nil
    private let mediaRepository: MediaRepository
    private var statusTask:  Task<(), any Error>? = nil
    
    let errorChannel: ErrorChannel = AppErrorKt.buildErrorChannel()
    
    init() {
        print("DiscoverViewModel init")
        dataProvider = KoinHelper.shared.discoverDataProvider()
        mediaRepository = KoinHelper.shared.mediaRepository()
        statusTask = Task { [weak self] in
            guard let stream = self?.dataProvider.getdiscoverUiStateAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
        
        cancelLastAndRegisterUiSideEffect(force: false, completer: nil)
    }
    
    deinit {
        statusTask?.cancel()
        sideEffectTask?.cancel()
        print("DiscoverViewModel deinit")
    }
    
    func doRefreshAndAwait() async throws {
        refreshCompleter?.cancel()
        refreshCompleter = OneShotCompleter<Void>()
        cancelLastAndRegisterUiSideEffect(force: true, completer: refreshCompleter)
        try await refreshCompleter?.wait()
    }
    
    func cancelLastAndRegisterUiSideEffect(force: Bool, completer: OneShotCompleter<Void>?) {
        sideEffectTask?.cancel()
        var isCompleted = false
        sideEffectTask = Task { [weak self] in
            guard let stream = self?.dataProvider.discoverUiSideEffectStatusSequence(force) else { return }
            
            do {
                for try await status in stream {
                    guard let self = self else { continue }

                    AppErrorKt.submitErrorOfSyncStatus(self.errorChannel, status: status)
                    
                    print("Discover cancelLastAndRegisterUiSideEffect status: \(status)")
                    if completer != nil && !status.isLoading() && !isCompleted {
                        print("Discover cancelLastAndRegisterUiSideEffect refresh completed")
                        completer?.complete(())
                        isCompleted = true
                    }
                }
            } catch is CancellationError {
                print("Discover cancelLastAndRegisterUiSideEffect Canceled.")
            } catch {
                print("Discover cancelLastAndRegisterUiSideEffect Failed with error: \(error)")
            }
        }
    }
}

struct DiscoverView: View {
    @StateObject private var viewModel = DiscoverViewModel()
    @StateObject private var snackbarManager = SnackbarManager()
    @EnvironmentObject var router: Router
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 20) {
                if !viewModel.uiState.newReleasedMedia.isEmpty {
                    if #available(iOS 17.0, *) {
                        NewReleaseCard_iOS17(
                            items: viewModel.uiState.newReleasedMedia,
                            userTitleLanguage: viewModel.uiState.userOptions.titleLanguage
                        )
                        .padding(.bottom, 8)
                        .transition(.opacity)
                    }
                }
                ForEach(Array(viewModel.uiState.categoryDataMap.content), id: \ .category) { categoryWithContents in
                    TitleWithContent(title: categoryWithContents.category.title, onMoreClick: {
                        let category = categoryWithContents.category
                        router.navigateTo(route: AppRoute.mediaCategoryPaingList(category: category))
                    }) {
                        MediaPreviewSector(
                            mediaList: categoryWithContents.medias,
                            userTitleLanguage: viewModel.uiState.userOptions.titleLanguage) { item in
                                router.navigateTo(route: .detailMedia(mediaId: item.id))
                            }
                    }
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                }
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
        }
        .scrollContentBackground(.hidden)
        .background(Color(.systemGroupedBackground))
        .refreshable {
            do {
                try await viewModel.doRefreshAndAwait()
            } catch {
                print("Discover error when refresh \(error)")
            }
        }
        .snackbar(manager: snackbarManager)
        .errorHandling(source: viewModel.errorChannel, snackbarManager: snackbarManager)
        .navigationTitle("Discover")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {
                    Task {
                        try? await viewModel.doRefreshAndAwait()
                    }
                }) {
                    Image(systemName: "arrow.clockwise")
                }
                .accessibilityLabel("Refresh")
            }
        }
        .animation(.default, value: viewModel.uiState)
    }
}

struct MediaPreviewSector: View {
    let mediaList: [MediaModel]
    let userTitleLanguage: UserTitleLanguage
    let onMediaClick: (MediaModel) -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(mediaList, id: \.id) { media in
                    MediaPreviewItemWrapper(
                        media: media,
                        userTitleLanguage: userTitleLanguage,
                        onMediaClick: { media in onMediaClick(media) }
                    )
                    .frame(width: 150)
                }
            }
            .padding(.vertical, 4)
        }
    }
}

struct MediaPreviewItemWrapper: View {
    let media: MediaModel
    let userTitleLanguage: UserTitleLanguage
    let onMediaClick: (MediaModel) -> Void
    
    var body: some View {
        let title = media.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? ""
        MediaPreviewItem(
            title: title,
            isFollowing: false,
            coverImage: media.coverImage,
            onClick: { onMediaClick(media) }
        )
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: 2)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(title)
    }
}

struct TitleWithContent<Content: View>: View {
    let title: String
    let onMoreClick: () -> Void
    let content: () -> Content
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(title)
                    .lineLimit(1)
                    .font(.title3).fontWeight(.bold)
                Spacer()
                Button(action: onMoreClick) {
                    Label("More", systemImage: "chevron.right")
                        .labelStyle(.titleAndIcon)
                }
                .buttonStyle(.bordered)
                .font(.subheadline)
            }
            .padding(.horizontal, 4)
            .padding(.vertical, 4)
            content()
        }
    }
}

extension MediaCategory {
    var title: String {
        switch self {
        case .currentSeasonAnime:
            return "Popular this season"
        case .nextSeasonAnime:
            return "Upcoming next season"
        case .trendingAnime:
            return "Trending now"
        case .movieAnime:
            return "Movie"
        case .theNewAddedAnime:
            return "New Added Anime"
        case .trendingManga:
            return "TODO"
        case .allTimePopularManga:
            return "TODO"
        case .topManhwa:
            return "TODO"
        case .theNewAddedManga:
            return "TODO"
        default: fatalError("NEVER")
        }
    }
}
