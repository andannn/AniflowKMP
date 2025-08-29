import SwiftUI
import Combine
import Shared

@MainActor
final class NotificationViewModel: ObservableObject {
    @Published private(set) var selectedCategory: NotificationCategory = .all
    
    @Published var pagingComponent: NotificationPageComponent?
    
    private var cancellables = Set<AnyCancellable>()
    
    init() {
        $selectedCategory
            .removeDuplicates()
            .sink { [weak self] category in
                guard let self else { return }
                
                print("NotificationViewModel selectedCategory changed: \(category)")
                self.pagingComponent?.dispose()
                self.pagingComponent = PageComponentFactory.shared.createNotificationPageComponent(category: category)
            }
            .store(in: &cancellables)
    }
    
    func selectCategory(_ category: NotificationCategory) {
        selectedCategory = category
    }
    
    deinit {
        print("NotificationViewModel deinit")
        cancellables.removeAll()
    }
}

struct Notification: View {
    @StateObject private var viewModel = NotificationViewModel()
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
                FilterMenu(
                    onSelectCategory: { category in
                        viewModel.selectCategory(category)
                    }, selectedCategory: viewModel.selectedCategory
                )
            }
        }
    }
}

struct FilterMenu: View {
    let onSelectCategory: (NotificationCategory) -> Void
    let selectedCategory: NotificationCategory
    
    var body: some View {
        Menu {
            ForEach(NotificationCategory.entries, id: \.self) { cat in
                Button {
                    onSelectCategory(cat)
                } label: {
                    if cat == selectedCategory {
                        Label(cat.label, systemImage: "checkmark")
                    } else {
                        Text(cat.label)
                    }
                }
            }
        } label: {
            HStack(spacing: 8) {
                Image(systemName: "line.3.horizontal.decrease.circle")
                Text(selectedCategory.label)
            }
            .padding(16)
            .contentShape(Rectangle())
        }
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
