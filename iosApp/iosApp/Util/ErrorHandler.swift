
import Shared
import SwiftUI

extension View {
    func errorHandling(source: AppErrorSource, snackbarManager: SnackbarManager) -> some View {
        modifier(ErrorHandleModifier(source: source, snackbarManager: snackbarManager))
    }
}

@MainActor
class ErrorHandler {
    private let snackBarManager: SnackbarManager
    
    init(snackBarManager: SnackbarManager) {
        self.snackBarManager = snackBarManager
    }
    
    func handleError(error: AppError) async -> SnackbarResult {
        let snackBarMessage = error.toAlert()
        return await snackBarManager.show(snackBarMessage.message, actionTitle: snackBarMessage.actionLabel, duration: snackBarMessage.duration.timeInterval)
    }
}

private struct ErrorHandleModifier: ViewModifier {
    let source: AppErrorSource
    let snackbarManager: SnackbarManager
    @State private var task: Task<Void, Never>? = nil
    
    func body(content: Content) -> some View {
        let errorHandler = ErrorHandler(snackBarManager: snackbarManager)
        
        content
            .task(id: ObjectIdentifier(source as AnyObject)) {
                task?.cancel()
                task = Task {
                    do {
                        for try await errors in source.getErrorAsyncSequence() {
                            for err in errors {
                                await errorHandler.handleError(error: err)
                            }
                        }
                    } catch is CancellationError {
                    } catch {
                    }
                }
            }
    }
}

extension SnackbarShowDuration {
    var timeInterval: TimeInterval? {
        switch self {
        case .short_:
            return 2.0
        case .long_:
            return 3.5
        case .indefinite:
            return nil
        default: fatalError("Never")
        }
    }
}
