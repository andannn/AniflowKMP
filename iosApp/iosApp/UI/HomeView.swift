import UIKit
import SwiftUI
import Shared

struct HomeView: View {
    private let component: HomeComponent
    
    @StateValue
    private var stack: ChildStack<AnyObject, HomeComponentChild>
    
    @StateValue
    private var selectedNavigationItem: TopLevelNavigation
    
    private var activeChild: HomeComponentChild { stack.active.instance }
    
    init(_ component: HomeComponent) {
        self.component = component
        _stack = StateValue(component.stack)
        _selectedNavigationItem = StateValue(component.selectedNavigationItem)
    }
    
    var body: some View {
        VStack {
            StackView(
                stackValue: _stack,
                getTitle: { _ in "Heh" },
                onBack: { a in  }
            ) { child in
                ChildView(child: child)
                    .frame(maxHeight: .infinity)
            }
            
            NavigationArea(selected: selectedNavigationItem) { newItem in
                component.onSelectNavigationItem(navigationItem: newItem)
            }
        }
    }
}

private struct ChildView: View {
    let child: HomeComponentChild
    
    var body: some View {
        switch child {
        case let child as HomeComponentChildDiscover: DiscoverView(child.component)
        default: EmptyView()
        }
    }
}

private struct NavigationArea: View {
    let selected: TopLevelNavigation
    let onItemClick: (TopLevelNavigation) -> Void
    
    var body: some View {
        HStack {
            ForEach(TopLevelNavigation.entries, id: \.self) { item in
                Button(action: { onItemClick(item) }) {
                    VStack(spacing: 4) {
                        Image(systemName: selected == item ? item.selectedIcon : item.unselectedIcon)
                            .imageScale(.large)
                        Text(item.label)
                            .font(.caption)
                    }
                    .foregroundColor(selected == item ? .blue : .gray)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .padding(.vertical, 8)
        .background(Color(.systemGray6))
    }
}

private struct VerticalLabelStyle: LabelStyle {
    func makeBody(configuration: Configuration) -> some View {
        VStack(alignment: .center, spacing: 8) {
            configuration.icon
            configuration.title
        }
    }
}

extension TopLevelNavigation {
    var selectedIcon: String {
        switch self {
        case .discover: return "sparkles"
        case .track: return "bookmark.fill"
        case .social: return "bubble.left.and.bubble.right.fill"
        case .profile: return "person.fill"
        default: return ""
        }
    }
    
    var unselectedIcon: String {
        switch self {
        case .discover: return "sparkles"
        case .track: return "bookmark"
        case .social: return "bubble.left.and.bubble.right"
        case .profile: return "person"
        default: return ""
        }
    }
    
    var label: String {
        switch self {
        case .discover: return "Discover"
        case .track: return "Track"
        case .social: return "Social"
        case .profile: return "Profile"
        default: return ""
        }
    }
}
