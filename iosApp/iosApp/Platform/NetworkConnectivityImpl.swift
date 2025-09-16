import Foundation
import Network
import Shared

final class NetworkConnectivityImpl: NetworkConnectivity {
    private let monitor = NWPathMonitor()
    private let monitorQueue = DispatchQueue(label: "net.monitor.queue")
    private let stateQueue = DispatchQueue(label: "net.state.queue", attributes: .concurrent)

    private var _isConnected: Bool = false
    private func setConnected(_ v: Bool) {
        stateQueue.async(flags: .barrier) { self._isConnected = v }
    }
    private func getConnected() -> Bool {
        stateQueue.sync { _isConnected }
    }

    init() {
        setConnected(false)

        monitor.pathUpdateHandler = { [weak self] path in
            guard let self = self else { return }
            let ok = (path.status == .satisfied)
            self.setConnected(ok)
        }
        monitor.start(queue: monitorQueue)
    }

    deinit {
        monitor.cancel()
    }

    func isConnected() -> Bool {
        return getConnected()
    }
}
