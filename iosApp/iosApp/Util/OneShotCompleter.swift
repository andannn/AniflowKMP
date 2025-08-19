import Foundation

@MainActor
class OneShotCompleter<T> {
    enum State { case pending, done }
    private var state: State = .pending
    private var continuation: CheckedContinuation<T, Error>?

    func wait() async throws -> T {
        try await withCheckedThrowingContinuation { (cont: CheckedContinuation<T, Error>) in
            if state == .done {
                cont.resume(throwing: CancellationError())
            } else {
                continuation = cont
            }
        }
    }

    func complete(_ value: T) {
        guard state == .pending, let cont = continuation else { return }
        state = .done
        continuation = nil
        cont.resume(returning: value)
    }

    func fail(_ error: Error) {
        guard state == .pending, let cont = continuation else { return }
        state = .done
        continuation = nil
        cont.resume(throwing: error)
    }

    func cancel() {
        guard state == .pending, let cont = continuation else { return }
        state = .done
        continuation = nil
        cont.resume(throwing: CancellationError())
    }
}
