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
    
    init() {
        print("DiscoverViewModel init")
        dataProvider = KoinHelper.shared.discoverDataProvider()
        mediaRepository = KoinHelper.shared.mediaRepository()
        Task {
            do {
                for try await state in dataProvider.getdiscoverUiStateAsyncSequence() {
                    print("Discover contentMode change: \(state.contentMode)")
                    uiState = state
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
        
        cancelLastAndRegisterUiSideEffect(completer: nil)
    }
    
    deinit {
        print("DiscoverViewModel deinit")
    }
    
    func doRefreshAndAwait() async throws {
        refreshCompleter?.cancel()
        refreshCompleter = OneShotCompleter<Void>()
        cancelLastAndRegisterUiSideEffect(completer: refreshCompleter)
        try await refreshCompleter?.wait()
    }
    
    func cancelLastAndRegisterUiSideEffect(completer: OneShotCompleter<Void>?) {
        sideEffectTask?.cancel()
        var isCompleted = false
        sideEffectTask = Task {
            do {
                for try await status in dataProvider.discoverUiSideEffectStatusSequence() {
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
    
    func setContentMode(mode: MediaContentMode) {
        print("Discover setContentMode \(mode)")
        Task {
            try await mediaRepository.setContentMode(mode: mode)
        }
    }
}

struct DiscoverView: View {
    @StateObject private var viewModel = DiscoverViewModel()
    @EnvironmentObject var router: Router
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 16) {
                Toggle(
                    isOn: Binding(
                        get: { viewModel.uiState.contentMode == .anime },
                        set: { check in
                            if check {
                                viewModel.setContentMode(mode: .anime)
                            } else {
                                viewModel.setContentMode(mode: .manga)
                            }
                        }
                    )
                ) {
                    Text("AAAAA")
                }
                .toggleStyle(SwitchToggleStyle(tint: .blue))
                
                ForEach(Array(viewModel.uiState.categoryDataMap.content), id: \.category) { categoryWithContents in
                    TitleWithContent(title: categoryWithContents.category.title, onMoreClick: {
                        let category = categoryWithContents.category
                        router.navigateTo(route: AppRoute.mediaCategoryPaingList(category: category))
                    }) {
                        MediaPreviewSector(mediaList: categoryWithContents.medias) { item in
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
        .navigationTitle("Discover")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {
                    // TODO:
                }) {
                    if let avatarUrl = viewModel.uiState.authedUser?.avatar {
                        AsyncImage(url: URL(string: avatarUrl)) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                        } placeholder: {
                            ProgressView()
                        }
                        .frame(width: 32, height: 32)
                        .clipShape(Circle())
                    } else {
                        Image(systemName: "person.crop.circle")
                            .font(.system(size: 24))
                    }
                }
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
                    MediaPreviewItem(
                        title: media.title?.english ?? "EEEEEEEEEE",
                        isFollowing: false,
                        coverImage: media.coverImage,
                        onClick: { onMediaClick(media) }
                    )
                    .frame(width: 240)
                }
            }
            .frame(maxWidth: .infinity)
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
