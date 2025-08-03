import UIKit
import SwiftUI
import Shared

struct RootView: View {
    private let root: RootComponent
    
    init(_ root: RootComponent) {
        self.root = root
    }

    var body: some View {
        StackView(
            stackValue: StateValue(root.stack),
            getTitle: { _ in "Heh" },
            onBack: { a in  }
        ) { child in
            switch child {
            case let child as RootComponentChildHome: HomeView(child.component)
            default: EmptyView()
            }
        }
    }
}
