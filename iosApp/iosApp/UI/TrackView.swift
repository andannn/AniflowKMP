import Shared
import SwiftUI

struct TrackView: View {
    private let component: TrackComponent

    @StateValue
    private var content : TrackComponentContent

    init(_ component: TrackComponent) {
        self.component = component
        _content = StateValue(component.content)
    }

    var body: some View {
        TrackContent(
            content.items
        )
    }
}

struct TrackContent: View {
    private let items: [DataMediaWithMediaListItem]

    init(_ items:  [DataMediaWithMediaListItem]) {
        self.items = items
    }

    var body: some View {
        List(items, id: \.self.mediaListModel.id) { item in
            Text(item.mediaModel.title?.romaji ?? "value")
        }
    }
}
