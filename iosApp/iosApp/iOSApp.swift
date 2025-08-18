import SwiftUI
import Shared

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    var body: some Scene {
        WindowGroup {
            RootView()
                .onOpenURL { url in
                    appDelegate.authHandler.onOpenURL(url)
                }
                .onReceive(NotificationCenter.default.publisher(for: UIScene.didActivateNotification)) { _ in
                    appDelegate.authHandler.onSceneDidBecomeActive()
                }
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    let authHandler: BrowserAuthOperationHandlerImpl = BrowserAuthOperationHandlerImpl()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        KoinHelper.shared.startKoin(
            modules: KoinHelper.shared.Modules,
            browserAuthOperationHandler: authHandler
        )
        
        #if DEBUG
        print("Running in Debug mode")
        Logger.shared.enableDebugLog()
        #else
        print("Running in Release mode")
        #endif

        return true
    }
}
