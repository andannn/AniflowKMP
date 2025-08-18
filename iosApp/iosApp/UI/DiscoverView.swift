import UIKit
import SwiftUI
import Shared

@MainActor
class DiscoverViewModel: ObservableObject {
    private let dataProvider: DataProviderWrapper
    @Published public var uiState: DiscoverUiState = DiscoverUiState.companion.Empty
    
    init() {
        print("DiscoverViewModel init")
        dataProvider = DataProviderWrapper(ktDataProvider: KoinHelper.shared.dataProvider())
        Task {
            do {
                for try await state in dataProvider.getdiscoverUiStateAsyncSequence() {
                    uiState = state
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
        
        Task {
            do {
                for try await error in dataProvider.discoverUiSideEffectErrorSequence() {
                    // handle side effect error.
                    print("error \(error)")
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    deinit {
        print("DiscoverViewModel deinit")
    }
}

struct DiscoverView: View {
    @StateObject private var viewModel = DiscoverViewModel()
    
    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVStack(alignment: .leading, spacing: 16) {
                    ForEach(Array(viewModel.uiState.categoryDataMap.content), id: \.category) { categoryWithContents in
                        TitleWithContent(title: categoryWithContents.category.title, onMoreClick: {}) {
                            MediaPreviewSector(mediaList: categoryWithContents.medias) { item in
                                // onMediaClick
                                // component.onMediaClick(media: item)
                            }
                        }
                    }
                }
                .padding()
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
