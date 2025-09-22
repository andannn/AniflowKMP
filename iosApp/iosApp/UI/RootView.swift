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
                    case .home:
                        fatalError()
                    case .mediaCategoryPaingList(let category):
                        MediaCategoryPaging(category: category)
                    case .notification:
                        Notification()
                        
                    case .stateObjOrObservableObj:
                        ContentView()
                    case .detailMedia(let mediaId):
                        DetailMediaView(mediaId: mediaId)
                    case .settings:
                        SettingView()
                    case .detailCharacter(characterId: let characterId):
                        DetailCharacterView(characterId: characterId)
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
    case notification
    case settings
    case detailCharacter(characterId: String)
    case detailMedia(mediaId: String)
    
    // Demo:
    case stateObjOrObservableObj
}
