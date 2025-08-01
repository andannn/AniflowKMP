import SwiftUI
import Shared

@main
struct iOSApp: App {
    lazy var root: RootComponent = DefaultRootComponent(
        componentContext: DefaultComponentContext()
        
    )
    
    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
