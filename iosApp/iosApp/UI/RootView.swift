import UIKit
import SwiftUI
import Shared

struct RootView: View {
    @StateObject private var router = Router()
    
    var body: some View {
        NavigationStack(path: $router.path) {
            HomeView()
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case .mediaCategoryPaingList(let category):
                        MediaCategoryPaging(category: category)
                    default:
                        fatalError()
                    }
                }
        }
        .environmentObject(router)
    }
}

@MainActor
final class Router: ObservableObject {
    @Published var path = NavigationPath()
    
    func navigateTo(route: AppRoute) {
        path.append(route)
    }
    
    func pop() { path.removeLast() }
    func popToRoot() { path = NavigationPath() }
}

enum AppRoute: Hashable {
    case home
    case mediaCategoryPaingList(category: MediaCategory)
}
