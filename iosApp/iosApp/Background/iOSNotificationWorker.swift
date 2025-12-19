import Foundation
import UserNotifications
import Shared

struct iOSNotificationWorker {
    func doWork() async -> Bool {
        print("[iOSNotificationWorker] doWork E")

        let result: SyncResult
        do {
            result = try await KoinExtension.shared.notificationFetchTask().sync()
        } catch {
            print("[iOSNotificationWorker] Fetch threw error: \(error)")
            return false
        }

        switch result {
        case is SyncResultFailure:
            print("[iOSNotificationWorker] Failure")
            return false

        case is SyncResultRetry:
            print("[iOSNotificationWorker] Retry")
            return false

        case let success as SyncResultSuccess<NotificationResult>:
            print("[iOSNotificationWorker] Success")

            let payload = success.result
            let notifications = payload?.notifications ?? []
            for n in notifications {
//                await NotificationHelperIOS().sendNotification(
//                    PlatformNotification(id: n.id, title: n.title, body: n.body)
//                )
            }
            return true

        default:
            return false
        }
    }
}
