import Shared

import Foundation
import UIKit
import Foundation
import UIKit

private let TAG = "BrowserAuthOperationHandler"
private let CLIENT_ID = "14409"
private let AUTH_URL = "https://anilist.co/api/v2/oauth/authorize?client_id={client_id}&response_type=token"

class BrowserAuthOperationHandlerImpl: DataBrowserAuthOperationHandler {
    private var continuation: CheckedContinuation<DataAuthToken, Error>?
    
    func openBrowser() {
        guard let url = URL(string: AUTH_URL.replacingOccurrences(of: "{client_id}", with: CLIENT_ID)) else {
            print("[\(TAG)] Failed to create URL")
            return
        }
        
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
    
    func awaitAuthResult() async throws -> DataAuthToken {
        return try await withCheckedThrowingContinuation { cont in
            if self.continuation != nil {
                cont.resume(throwing: NSError(domain: "AuthAlreadyInProgress", code: 1, userInfo: nil))
                return
            }
            self.continuation = cont
        }
    }
    
    func handleOpenURL(_ url: URL) {
        print("[\(TAG)] handleOpenURL: \(url)")
        
        guard url.scheme == "animetracker" else { return }
        
        let components = URLComponents(string: url.absoluteString.replacingOccurrences(of: "#", with: "?"))
        guard
            let accessToken = components?.queryItems?.first(where: { $0.name == "access_token" })?.value,
            let expiresIn = components?.queryItems?.first(where: { $0.name == "expires_in" })?.value,
            let expiresInInt = Int(expiresIn)
        else {
            print("[\(TAG)] Missing token or expiry")
            return
        }
        
        let token = DataAuthToken(token: accessToken, expiresInTime: Int32(expiresInInt))
        
        if let cont = continuation {
            cont.resume(returning: token)
        } else {
            print("[\(TAG)] Auth result received but no continuation.")
        }
        
        continuation = nil
    }
    
    func onSceneDidBecomeActive() {
        if continuation != nil {
            print("[\(TAG)] Scene resumed with no token, cancelling auth operation")
            continuation?.resume(throwing: NSError(domain: "AuthCancelled", code: 2, userInfo: nil))
            continuation = nil
        }
    }
}
