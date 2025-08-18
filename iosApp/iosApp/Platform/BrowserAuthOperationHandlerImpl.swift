import Shared

import Foundation
import UIKit
import Foundation
import UIKit

class BrowserAuthOperationHandlerImpl: BrowserAuthOperationHandler {
    private enum AuthState {
        case initial
        case waiting(callback: (AuthToken?) -> Void)
    }

    private var authState: AuthState = .initial
    private let clientId = "14409"
    private let callbackScheme = "animetracker"

    func getAuthResult(callBack callback: @escaping (AuthToken?) -> Void) {
        if case .waiting = authState {
            fatalError("Auth operation already in progress")
        }

        guard let url = makeAuthUrl() else {
            print("[Auth] Invalid auth URL.")
            callback(nil)
            return
        }

        authState = .waiting(callback: callback)

        UIApplication.shared.open(url)
    }

    func cancel() {
        if case let .waiting(callback) = authState {
            print("[Auth] Auth operation canceled.")
            callback(nil)
            authState = .initial
        }
    }

    private func makeAuthUrl() -> URL? {
        let urlString = "https://anilist.co/api/v2/oauth/authorize?client_id=\(clientId)&response_type=token"
        return URL(string: urlString)
    }

  
    func onOpenURL(_ url: URL) {
        print("[Auth] Received URL: \(url.absoluteString)")

        guard url.scheme == callbackScheme else {
            print("[Auth] Ignored URL with mismatched scheme.")
            return
        }

        guard case let .waiting(callback) = authState else {
            print("[Auth] No auth operation in progress.")
            return
        }

        authState = .initial
        
        let rawUrl = URL(string: url.absoluteString.replacingOccurrences(of: "#", with: "?"))!
        guard let components = URLComponents(url: rawUrl, resolvingAgainstBaseURL: false),
              let token = components.queryItems?.first(where: { $0.name == "access_token" })?.value,
              let expiresStr = components.queryItems?.first(where: { $0.name == "expires_in" })?.value,
              let expires = Int(expiresStr)
        else {
            print("[Auth] Failed to parse token.")
            callback(nil)
            return
        }

        callback(AuthToken(token: token, expiresInTime: Int32(expires)))
    }
    
    func onSceneDidBecomeActive() {
        if case let .waiting(callback) = authState {
            print("[Auth] Scene resumed, no token received. Assuming cancel.")
            callback(nil)
            authState = .initial
        }
    }
}
