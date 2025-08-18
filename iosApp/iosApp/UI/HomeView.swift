import UIKit
import SwiftUI
import Shared

struct HomeView: View {
    @State
    private var selection: TopLevelNavigation = .discover
    
    var body: some View {
        NavigationStack {
            TabView(selection: $selection) {
                ForEach([TopLevelNavigation.discover,
                         .track,
                         .social,
                         .profile
                ], id: \.self) { tab in
                    screen(for: tab)
                        .tabItem {
                            Image(systemName: selection == tab ? tab.selectedIcon : tab.unselectedIcon)
                            Text(tab.label)
                        }
                        .tag(tab)
                }
            }
            .navigationTitle(selection.label)
        }
    }
    
    @ViewBuilder
    private func screen(for tab: TopLevelNavigation) -> some View {
        switch tab {
        case .discover:
            DiscoverView()
        case .track:
            TrackView()
            //        case .social:
            //            SocialView()
            //        case .profile:
            //            ProfileView()
        default:
            VStack {
                Text("Dummy")
            }
        }
    }
}

enum TopLevelNavigation {
    case discover
    case track
    case social
    case profile
    
    var selectedIcon: String {
        switch self {
        case .discover: return "sparkles"
        case .track: return "bookmark.fill"
        case .social: return "bubble.left.and.bubble.right.fill"
        case .profile: return "person.fill"
        }
    }
    
    var unselectedIcon: String {
        switch self {
        case .discover: return "sparkles"
        case .track: return "bookmark"
        case .social: return "bubble.left.and.bubble.right"
        case .profile: return "person"
        }
    }
    
    var label: String {
        switch self {
        case .discover: return "Discover"
        case .track: return "Track"
        case .social: return "Social"
        case .profile: return "Profile"
        }
    }
}

