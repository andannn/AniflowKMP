import UIKit
import SwiftUI
import Shared

struct RootView: View {
    @StateObject private var router = Router()
    
    var body: some View {
        NavigationStack(path: $router.path) {
            HomeView()
                .environmentObject(router)
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                        
                    default:
                        fatalError()
                    }
                }
        }
    }
}

@MainActor
final class Router: ObservableObject {
    @Published var path = NavigationPath()
    
    func pop() { path.removeLast() }
    func popToRoot() { path = NavigationPath() }
}

enum AppRoute: Hashable {
    case home
}
