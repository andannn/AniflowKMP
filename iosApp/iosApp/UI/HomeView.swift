import UIKit
import SwiftUI
import Shared

struct HomeView: View {
    @State
    private var selection: TopLevelNavigation = .discover
    @EnvironmentObject var router: Router

    var body: some View {
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
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {
                    router.showAuthDialog()
                }) {
//                    if let avatarUrl = viewModel.uiState.authedUser?.avatar {
//                        AsyncImage(url: URL(string: avatarUrl)) { image in
//                            image
//                                .resizable()
//                                .aspectRatio(contentMode: .fill)
//                        } placeholder: {
//                            ProgressView()
//                        }
//                        .frame(width: 32, height: 32)
//                        .clipShape(Circle())
//                    } else {
                        Image(systemName: "person.crop.circle")
                            .font(.system(size: 24))
//                    }
                }
            }
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

