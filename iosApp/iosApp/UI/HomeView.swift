import UIKit
import SwiftUI
import Shared

struct HomeView: View {
    private let component: HomeComponent
    
    init(_ component: HomeComponent) {
        self.component = component
    }

    var body: some View {
        StackView(
            stackValue: StateValue(component.stack),
            getTitle: { _ in "Heh" },
            onBack: { a in  }
        ) { child in
            switch child {
            case let child as HomeComponentChildDiscover: DiscoverView(child.component)
            default: EmptyView()
            }
        }
    }
}
