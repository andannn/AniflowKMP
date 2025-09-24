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
                    NewReleaseCardSimple(
                        items: viewModel.uiState.newReleasedMedia,
                        userTitleLanguage: viewModel.uiState.userOptions.titleLanguage,
                        onItemClick: { item in
                            router.navigateTo(route: .detailMedia(mediaId: item.mediaModel.id))
                        }
                    )
                    .padding(.bottom, 8)
                    .transition(.asymmetric(
                        insertion: .move(edge: .trailing).combined(with: .opacity),
                        removal: .move(edge: .leading).combined(with: .opacity)
                    ))
                }
                ForEach(Array(viewModel.uiState.categoryDataMap.content), id: \ .category) { categoryWithContents in
                    VStack{
                        SectionHeader(title: categoryWithContents.category.title_, showMore: true, onMore: {
                            let category = categoryWithContents.category
                            router.navigateTo(route: AppRoute.mediaCategoryPaingList(category: category))
                        })
                        MediaPreviewSector(
                            mediaList: categoryWithContents.medias,
                            userTitleLanguage: viewModel.uiState.userOptions.titleLanguage
                        ) { item in
                            router.navigateTo(route: .detailMedia(mediaId: item.id))
                        }
                    }
                }
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
        }
        .scrollContentBackground(.hidden)
        .refreshable {
            do {
                try await viewModel.doRefreshAndAwait()
            } catch {
                print("Discover error when refresh \(error)")
            }
        }
        .snackbar(manager: snackbarManager)
        .errorHandling(source: viewModel.errorChannel, snackbarManager: snackbarManager)
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
    }
}

struct MediaPreviewSector: View {
    let mediaList: [MediaModel]
    let userTitleLanguage: UserTitleLanguage
    let onMediaClick: (MediaModel) -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 6) {
                ForEach(mediaList, id: \.id) { media in
                    let title = media.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? ""
                    MediaPreviewItem(
                        title: title,
                        coverImage: media.coverImage,
                        onClick: { onMediaClick(media) }
                    )
                    .frame(width: 150)
                }
            }
        }
    }
}
