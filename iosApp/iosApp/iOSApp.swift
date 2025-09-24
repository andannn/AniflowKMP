import SwiftUI
import Shared
import BackgroundTasks
import FirebaseCore

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
    let netWorkConnectivity: NetworkConnectivityImpl = NetworkConnectivityImpl()
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        FirebaseApp.configure()
        KoinHelper.shared.startKoin(
            modules: KoinHelper.shared.Modules,
            browserAuthOperationHandler: authHandler,
            networkConnectivity: netWorkConnectivity,
            platformAnalytics: IOSAnalytics()
        )
        
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "me.aniflow.notifications", using: nil) { task in
            handleRefreshTask(task: task as! BGAppRefreshTask)
        }
        scheduleRefresh()
        
#if DEBUG
        print("Running in Debug mode")
        Logger.shared.enableDebugLog()
#else
        print("Running in Release mode")
#endif
        return true
    }
}


func scheduleRefresh() {
    print("scheduleRefresh")
    
    let request = BGAppRefreshTaskRequest(identifier: "me.aniflow.notifications")
    request.earliestBeginDate = Date(timeIntervalSinceNow: 60 * 60)
    do {
        try BGTaskScheduler.shared.submit(request)
    } catch {
        print("Submit BGAppRefreshTask failed: \(error)")
    }
}

func handleRefreshTask(task: BGAppRefreshTask) {
    scheduleRefresh()
    
    print("handleRefreshTask: \(task)")
    let notificationTask = Task {
        let result = await iOSNotificationWorker().doWork()
        task.setTaskCompleted(success: result)
    }
    
    task.expirationHandler = {
        notificationTask.cancel()
    }
}
