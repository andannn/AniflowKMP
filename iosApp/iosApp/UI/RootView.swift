import UIKit
import SwiftUI
import Shared

@MainActor
class AppViewModel: ObservableObject {
    
    private let authRepository: AuthRepository = KoinHelper.shared.authRepository()
    
    private var loginTask : Task<(), any Error>? = nil
    private var logoutTask : Task<(), any Error>? = nil
    
    func handleLogin() {
        print("AppViewModel handleLogin ")
        loginTask?.cancel()
        loginTask = Task {
            print("AppViewModel start ")
            let appError = try await authRepository.startLoginProcessAndWaitResult()
            print("AppViewModel end appError? \(String(describing: appError?.message)) ")
        }
    }
    
    func handleLogout() {
        logoutTask?.cancel()
        logoutTask = Task {
            print("AppViewModel handleLogout")
            try await authRepository.logout()
        }
    }
}

struct RootView: View {
    @StateObject private var viewModel = AppViewModel()
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
                    case .mediaStaffPaging(mediaId: let mediaId):
                        DetailMediaStaffPaging(mediaId: mediaId)
                    case .detailStaff(staffId: let staffId):
                        DetailStaffView(staffId: staffId)
                    case .mediaCharacterPaging(mediaId: let mediaId):
                        DetailMediaCharacterPaging(mediaId: mediaId)
                    }
                }
        }
        .environmentObject(router)
        .customDialog(isPresented: $router.isAuthDialogShowing, content: {
            LoginDialogView(
                onLogout: {
                    viewModel.handleLogout()
                    router.closeAuthDialog()
                },
                onLogin: {
                    viewModel.handleLogin()
                    router.closeAuthDialog()
                },
                onSettingClick: {
                    router.navigateTo(route: .settings)
                    router.closeAuthDialog()
                },
                onNotificationClick: {
                    router.navigateTo(route: .notification)
                    router.closeAuthDialog()
                }
            )
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
    
    func closeAuthDialog() {
        isAuthDialogShowing = false
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
    case detailStaff(staffId: String)
    case mediaStaffPaging(mediaId: String)
    case mediaCharacterPaging(mediaId: String)
    
    // Demo:
    case stateObjOrObservableObj
}
