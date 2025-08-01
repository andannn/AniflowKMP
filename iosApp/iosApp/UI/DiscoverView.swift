import UIKit
import SwiftUI
import Shared

struct DiscoverView: View {
    private let component: DiscoverComponent

    @StateValue
    private var categoryDataMap: NSDictionary

    init(_ component: DiscoverComponent) {
        self.component = component
        _categoryDataMap = StateValue(component.categoryDataMap)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {

        }
        .padding()
    }
}
