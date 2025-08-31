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
                    // handle side effect status.
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
    @EnvironmentObject var router: Router
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 16) {
                if !viewModel.uiState.newReleasedMedia.isEmpty {
                    if #available(iOS 17.0, *) {
                        NewReleaseCard_iOS17(items: viewModel.uiState.newReleasedMedia)
                    } else {
                        // Fallback on earlier versions
                    }
                }
                
                ForEach(Array(viewModel.uiState.categoryDataMap.content), id: \.category) { categoryWithContents in
                    TitleWithContent(title: categoryWithContents.category.title, onMoreClick: {
                        let category = categoryWithContents.category
                        router.navigateTo(route: AppRoute.mediaCategoryPaingList(category: category))
                    }) {
                        MediaPreviewSector(mediaList: categoryWithContents.medias) { item in
                            router.navigateTo(route: .notification)
                            // onMediaClick
                            // component.onMediaClick(media: item)
                        }
                    }
                }
            }
            .padding()
        }
        .refreshable {
            do {
                try await viewModel.doRefreshAndAwait()
            } catch {
                print("Discover error when refresh \(error)")
            }
        }
    }
}

struct MediaPreviewSector: View {
    let mediaList: [MediaModel]
    let onMediaClick: (MediaModel) -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 0) {
                ForEach(mediaList, id: \.id) { media in
                    MediaPreviewItemWrapper(
                        media: media,
                        onMediaClick: { media in onMediaClick(media) }
                    )
                    .frame(width: 240)
                }
            }
            .frame(maxWidth: .infinity)
        }
    }
}

struct MediaPreviewItemWrapper: View {
    let media: MediaModel
    let onMediaClick: (MediaModel) -> Void
    @State private var titleText: String = ""

    var body: some View {
        MediaPreviewItem(
            title: titleText,
            isFollowing: false,
            coverImage: media.coverImage,
            onClick: { onMediaClick(media) }
        )
        .task {
            for await t in userTitleStream(title: media.title!) {
                titleText = t
            }
        }
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
                    .font(.headline)
                
                Spacer()
                
                Button(action: onMoreClick) {
                    Text("More")
                }
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 6)
            
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
