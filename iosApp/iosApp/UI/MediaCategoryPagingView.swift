import Shared
import SwiftUI

@MainActor
class MediaCategoryPagingViewModel: ObservableObject {
    let category: MediaCategory
    let pagingComponent: MediaCategoryPageComponent
    let authRepository: AuthRepository = KoinHelper.shared.authRepository()
    @Published var userOptions: UserOptions = UserOptions.companion.Default
    private var userOptionTask:  Task<(), any Error>? = nil

    init(category: MediaCategory) {
        self.category = category
        pagingComponent = PageComponentFactory.shared.createMediaCategoryPageComponent(category: category)
        
        userOptionTask = Task { [weak self] in
            guard let stream = self?.authRepository.getUserOptionsAsyncSequence() else { return }
               
            for try await state in stream {
                self?.userOptions = state
            }
        }
    }
    
    deinit {
        print("MediaCategoryPagingViewModel deinit")
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
                MediaPreviewItemWrapper(
                    media: media,
                    userTitleLanguage: viewModel.userOptions.titleLanguage,
                    onMediaClick: { media in  }
                )
            }
        )
        .navigationTitle(category.title)
        .navigationBarTitleDisplayMode(.inline)
    }
}
