import UIKit
import SwiftUI
import Shared

struct DiscoverView: View {
    private let component: DiscoverComponent
    
    @StateValue
    private var categoryDataMapHolder: CategoryDataModel
    
    init(_ component: DiscoverComponent) {
        self.component = component
        _categoryDataMapHolder = StateValue(component.categoryDataMap)
    }
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading, spacing: 16) {
                ForEach(Array(categoryDataMapHolder.map.keys), id: \.self) { category in
                    if let mediaList = categoryDataMapHolder.map[category] {
                        TitleWithContent(title: category.title, onMoreClick: {}) {
                            MediaPreviewSector(mediaList: mediaList) { item in
                                // onMediaClick
                            }
                        }
                    }
                }
            }
            .padding()
            
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
        case .trendingManga, .allTimePopularManga, .topManhwa, .theNewAddedManga:
            return "Popular this season"
        case .theNewAddedAnime:
            return "New Added Anime"
        default: return ""
        }
    }
}
