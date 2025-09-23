import SwiftUI
import Combine
import Shared

@MainActor
final class NotificationViewModel: ObservableObject {
    @Published private(set) var selectedCategory: NotificationCategory = .all
    
    @Published var pagingComponent: NotificationPageComponent?
    
    private var cancellables = Set<AnyCancellable>()
    let errorChannel: ErrorChannel = AppErrorKt.buildErrorChannel()

    init() {
        $selectedCategory
            .removeDuplicates()
            .sink { [weak self] category in
                guard let self = self else { return }
                
                print("NotificationViewModel selectedCategory changed: \(category)")
                self.pagingComponent?.dispose()
                self.pagingComponent = PageComponentFactory.shared.createNotificationPageComponent(category: category, errorHandler: self.errorChannel)
            }
            .store(in: &cancellables)
    }
    
    func selectCategory(_ category: NotificationCategory) {
        selectedCategory = category
    }
    
    deinit {
        print("NotificationViewModel deinit")
        cancellables.forEach { cancellable in
            cancellable.cancel()
        }
    }
}

struct Notification: View {
    @StateObject private var viewModel = NotificationViewModel()
    @StateObject private var snackbarManager = SnackbarManager()

    @EnvironmentObject private var router: Router
    let cols = [GridItem(.adaptive(minimum: 120), spacing: 12)]

    var body: some View {
        Group {
            if let pagingComponent = viewModel.pagingComponent {
                VerticalListPaging<NotificationModel, _>(
                    pageComponent: pagingComponent,
                    contentPadding: .init(top: 0, leading: 16, bottom: 0, trailing: 16),
                    key: { AnyHashable($0.id) },
                    itemContent: { notification in
                        NotificationItemView(
                            model: notification,
                            onNotificationClick: {
                                router.navigateTo(route: .stateObjOrObservableObj)
                            }
                        )
                    }
                ).id(viewModel.selectedCategory)
            } else {
                EmptyView()
            }
        }
        .navigationTitle("Notification")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NotificationFilterMenu(
                    selectedCategory: viewModel.selectedCategory,
                    onSelectCategory: { category in
                        viewModel.selectCategory(category)
                    }
                )
            }
        }
        .snackbar(manager: snackbarManager)
        .errorHandling(source: viewModel.errorChannel, snackbarManager: snackbarManager)
    }
}

extension NotificationCategory {
    var label: String {
        switch self {
        case .all: "All"
        case .airing: "Airing"
        case .activity: "Activity"
        case .follows: "Follows"
        case .media: "Media"
        default: fatalError()
        }
    }
}
