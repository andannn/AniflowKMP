import SwiftUI
import Shared

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    var body: some Scene {
        WindowGroup {
            RootView(appDelegate.root)
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
    lazy var root: RootComponent = DefaultRootComponent(
        componentContext: DefaultComponentContext(
            lifecycle: ApplicationLifecycle()
        )
    )
    
    let authHandler: BrowserAuthOperationHandlerImpl = BrowserAuthOperationHandlerImpl()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        KoinLauncher().startKoin(
            modules: KoinLauncherKt.Modules,
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
