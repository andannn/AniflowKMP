import SwiftUI
import Shared

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    var body: some Scene {
        WindowGroup {
            RootView(appDelegate.root)
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    lazy var root: RootComponent = DefaultRootComponent(
        componentContext: DefaultComponentContext(
            lifecycle: ApplicationLifecycle()
        )
    )
    
    let browserAuthOperationHandler:BrowserAuthOperationHandlerImpl = BrowserAuthOperationHandlerImpl()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        KoinLauncher().startKoin(
            modules: KoinLauncherKt.Modules,
            browserAuthOperationHandler: browserAuthOperationHandler
        )
        
        #if DEBUG
        print("Running in Debug mode")
        Logger.shared.enableDebugLog()
        #else
        print("Running in Release mode")
        #endif

        return true
    }

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        browserAuthOperationHandler.handleOpenURL(url)
        return true
    }
    
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        browserAuthOperationHandler.onSceneDidBecomeActive()
    }
}
