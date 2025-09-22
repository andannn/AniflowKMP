import UIKit
import SwiftUI
import Shared

@MainActor
class HomeViewModel: ObservableObject {
    @Published public var uiState: HomeAppBarUiState = HomeAppBarUiState.companion.Empty
    private let dataProvider: HomeAppBarUiDataProvider
    private let mediaRepository: MediaRepository
    private var dataTask : Task<(), any Error>? = nil
    
    init() {
        print("HomeViewModel init")
        dataProvider = KoinHelper.shared.homeAppBarUiDataProvider()
        mediaRepository = KoinHelper.shared.mediaRepository()
        
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.appBarAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
    }
    
    func setContentMode(mode: MediaContentMode) {
        print("Discover setContentMode \(mode)")
        Task {
            try await mediaRepository.setContentMode(mode: mode)
        }
    }
    
    func toggleContentMode() {
        if (uiState.contentMode == MediaContentMode.anime) {
            setContentMode(mode: .manga)
        } else {
            setContentMode(mode: .anime)
        }
    }
    
    deinit {
        print("HomeViewModel deinit")
        dataTask?.cancel()
    }
}

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    
    @State
    private var selection: TopLevelNavigation = .discover
    @EnvironmentObject var router: Router
    
    private var isAnimeBinding: Binding<Bool> {
        Binding<Bool>(
            get: { viewModel.uiState.contentMode == .anime },
            set: { viewModel.setContentMode(mode: $0 ? .anime : .manga) }
        )
    }
    
    var body: some View {
        TabView(selection: $selection) {
            ForEach([TopLevelNavigation.discover,
                     .track,
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
            ToolbarItem(placement: .principal) {
                ModeSwitchInToolbar(isAnime: isAnimeBinding)
            }
            
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    router.showAuthDialog()
                } label: {
                    if let avatarUrl = viewModel.uiState.authedUser?.avatar,
                       let url = URL(string: avatarUrl) {
                        AsyncImage(url: url) { image in
                            image.resizable().aspectRatio(contentMode: .fill)
                        } placeholder: {
                            ProgressView()
                        }
                        .frame(width: 32, height: 32)
                        .clipShape(Circle())
                    } else {
                        Image(systemName: "person.crop.circle")
                            .font(.system(size: 24))
                    }
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
        }
    }
}

enum TopLevelNavigation {
    case discover
    case track
    
    var selectedIcon: String {
        switch self {
        case .discover: return "sparkles"
        case .track: return "bookmark.fill"
        }
    }
    
    var unselectedIcon: String {
        switch self {
        case .discover: return "sparkles"
        case .track: return "bookmark"
        }
    }
    
    var label: String {
        switch self {
        case .discover: return "Discover"
        case .track: return "Track"
        }
    }
}

private struct ModeSwitchInToolbar: View {
    let isAnime: Binding<Bool>
    
    var body: some View {
        Picker("", selection: isAnime) {
            Image(systemName: "film.fill")
                .tag(true)
                .accessibilityLabel("Anime")
            Image(systemName: "book.closed.fill")
                .tag(false)
                .accessibilityLabel("Manga")
        }
        .pickerStyle(.segmented)
        .labelsHidden()
        .controlSize(.small)
        .frame(width: 140)
    }
}
