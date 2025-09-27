
import Shared

class SnackbarMessageHandlerImpl : SnackBarMessageHandler {
    private let snackbarManager: SnackbarManager
    
    init(snackbarManager: SnackbarManager) {
        self.snackbarManager = snackbarManager
    }
    
    func showSnackBarMessage(message: SnackBarMessage, callBack: @escaping (SharedSnackbarResult) -> Void) {
        Task {
            let result = await snackbarManager.show(message.message, actionTitle: message.actionLabel, duration: message.duration.timeInterval)
            callBack(result == .dismissed ? SharedSnackbarResult.dismissed : SharedSnackbarResult.actionperformed)
        }
    }
}
