import SwiftUI
import Shared

/// A small reusable filter menu component.
/// - Generic over an Item type (must be Hashable so it can be used as ForEach id).
/// - Caller provides a list of items, the currently selected item, how to render titles and optional icons,
///   and a selection callback.
struct FilterMenu<Item: Hashable>: View {
    let items: [Item]
    let selected: Item
    let onSelect: (Item) -> Void
    let title: (Item) -> String
    let icon: ((Item) -> Image?)?
    let menuIcon: Image?

    init(
        items: [Item],
        selected: Item,
        menuIcon: Image? = Image(systemName: "line.3.horizontal.decrease.circle"),
        icon: ((Item) -> Image?)? = nil,
        title: @escaping (Item) -> String,
        onSelect: @escaping (Item) -> Void
    ) {
        self.items = items
        self.selected = selected
        self.onSelect = onSelect
        self.title = title
        self.icon = icon
        self.menuIcon = menuIcon
    }

    var body: some View {
        Menu {
            ForEach(items, id: \.self) { item in
                Button {
                    onSelect(item)
                } label: {
                    HStack(spacing: 8) {
                        if let icon = icon?(item) {
                            icon
                        }
                        Text(title(item))
                        Spacer()
                        if item == selected {
                            Image(systemName: "checkmark")
                                .foregroundColor(.accentColor)
                        }
                    }
                }
            }
        } label: {
            HStack(spacing: 8) {
                if let menuIcon = menuIcon {
                    menuIcon
                }
                Text(title(selected))
            }
            .padding(.vertical, 6)
            .padding(.horizontal, 10)
            .contentShape(Rectangle())
        }
    }
}

// Concrete Notification filter menu (composition instead of extension)
struct NotificationFilterMenu: View {
    let selectedCategory: NotificationCategory
    let onSelectCategory: (NotificationCategory) -> Void

    var body: some View {
        FilterMenu<NotificationCategory>(
            items: NotificationCategory.entries,
            selected: selectedCategory,
            menuIcon: Image(systemName: "line.3.horizontal.decrease.circle"),
            icon: nil,
            title: { $0.label },
            onSelect: onSelectCategory
        )
    }
}

struct MediaSortFilterMenu: View {
    let selectedCategory: MediaSort
    let onSelectCategory: (MediaSort) -> Void

    var body: some View {
        FilterMenu<MediaSort>(
            items: MediaSort.entries,
            selected: selectedCategory,
            menuIcon: Image(systemName: "line.3.horizontal.decrease.circle"),
            icon: nil,
            title: { $0.label() },
            onSelect: onSelectCategory
        )
    }
}

struct MediaListOptionFilterMenu: View {
    let selectedCategory: MediaListStatus
    let onSelectCategory: (MediaListStatus) -> Void
    var options: [MediaListStatus]

    var body: some View {
        FilterMenu<MediaListStatus>(
            items: options,
            selected: selectedCategory,
            menuIcon: Image(systemName: "line.3.horizontal.decrease.circle"),
            icon: nil,
            title: { $0.label() },
            onSelect: onSelectCategory
        )
    }
}
