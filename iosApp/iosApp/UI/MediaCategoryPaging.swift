import Shared
import SwiftUI

@MainActor
class MediaCategoryPagingViewModel: ObservableObject {
    let category: MediaCategory
    let pagingComponent: MediaCategoryPageComponent
    
    init(category: MediaCategory) {
        self.category = category
        pagingComponent = PageComponentFactory.shared.createMediaCategoryPageComponent(category: category)
    }
}

struct MediaCategoryPaging: View {
    private let category: MediaCategory
    @StateObject private var viewModel: MediaCategoryPagingViewModel
    
    let cols = [GridItem(.adaptive(minimum: 120), spacing: 12)]

    init(category: MediaCategory) {
        print("MediaCategoryPaging init")
        _viewModel = StateObject(
            wrappedValue: MediaCategoryPagingViewModel(category: category)
        )
        self.category = category
    }
    
    var body: some View {
        VerticalGridPaging<MediaModel, _>(
            pageComponent: viewModel.pagingComponent,
            columns: cols,
            contentPadding: .init(top: 0, leading: 16, bottom: 0, trailing: 16),
            key: { AnyHashable($0.id) },
            itemContent: { media in
                MediaPreviewItem(
                    title: media.title?.english ?? "EEEEEEEEEE",
                    isFollowing: false,
                    coverImage: media.coverImage,
                    onClick: {  }
                )
            }
        )
        .navigationTitle(category.title)
        .navigationBarTitleDisplayMode(.inline)
    }
}
