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
        .customDialog(isPresented: $router.isAuthDialogShowing, content: {
            LoginDialogView()
        })
    }
}

@MainActor
final class Router: ObservableObject {
    @Published var path = NavigationPath()
    
    @Published var isAuthDialogShowing = false
    
    func navigateTo(route: AppRoute) {
        path.append(route)
    }
    
    func showAuthDialog() {
        isAuthDialogShowing = true
    }
    
    func pop() {
        if (isAuthDialogShowing) {
            isAuthDialogShowing = false
            return
        }

        path.removeLast()
    }
}

enum AppRoute: Hashable {
    case home
    case mediaCategoryPaingList(category: MediaCategory)
}
