import UIKit
import SwiftUI
import Shared

struct DiscoverView: View {
    private let component: DiscoverComponent
    
    @StateValue
    private var categoryDataMapHolder: CategoryDataModel
    
    @StateValue
    private var authedUser: Optional<DataUserModel>
    
    init(_ component: DiscoverComponent) {
        self.component = component
        _categoryDataMapHolder = StateValue(component.categoryDataMap)
        _authedUser = StateValue(component.authedUser)
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVStack(alignment: .leading, spacing: 16) {
                    ForEach(Array(categoryDataMapHolder.content), id: \.category) { categoryWithContents in
                        TitleWithContent(title: categoryWithContents.category.title, onMoreClick: {}) {
                            MediaPreviewSector(mediaList: categoryWithContents.medias) { item in
                                // onMediaClick
                                component.onMediaClick(media: item)
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
                    if let avatarUrl = authedUser.value?.avatar {
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
    let mediaList: [DataMediaModel]
    let onMediaClick: (DataMediaModel) -> Void
    
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

extension DataMediaCategory {
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
