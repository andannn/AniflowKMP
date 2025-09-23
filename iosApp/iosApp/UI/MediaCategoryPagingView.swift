import Shared
import SwiftUI

@MainActor
class MediaCategoryPagingViewModel: ObservableObject {
    let category: MediaCategory
    let pagingComponent: MediaCategoryPageComponent
    let authRepository: AuthRepository = KoinHelper.shared.authRepository()
    @Published var userOptions: UserOptions = UserOptions.companion.Default
    private var userOptionTask:  Task<(), any Error>? = nil

    let errorChannel: ErrorChannel = AppErrorKt.buildErrorChannel()

    init(category: MediaCategory) {
        self.category = category
        pagingComponent = PageComponentFactory.shared.createMediaCategoryPageComponent(category: category, errorHandler: errorChannel)
        
        userOptionTask = Task { [weak self] in
            guard let stream = self?.authRepository.getUserOptionsAsyncSequence() else { return }
               
            for try await state in stream {
                self?.userOptions = state
            }
        }
    }
    
    deinit {
        print("MediaCategoryPagingViewModel deinit")
        userOptionTask?.cancel()
    }
}

struct MediaCategoryPaging: View {
    private let category: MediaCategory
    @StateObject private var viewModel: MediaCategoryPagingViewModel
    @StateObject private var snackbarManager = SnackbarManager()
    @EnvironmentObject private var router: Router

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
                let title = media.title?.getUserTitleString(titleLanguage: viewModel.userOptions.titleLanguage) ?? ""
                MediaPreviewItem(
                    title: title,
                    coverImage: media.coverImage,
                    onClick: {
                        router.navigateTo(route: .detailMedia(mediaId: media.id))
                    }
                )
            }
        )
        .navigationTitle(category.title_)
        .navigationBarTitleDisplayMode(.inline)
        .snackbar(manager: snackbarManager)
        .errorHandling(source: viewModel.errorChannel, snackbarManager: snackbarManager)
    }
}
