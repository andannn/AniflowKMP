import SwiftUI
import Shared

@MainActor
final class HomeViewModel: ObservableObject {
    @Published private(set) var uiState: HomeAppBarUiState = HomeAppBarUiState.companion.Empty
    private let dataProvider: HomeAppBarUiDataProvider
    private let mediaRepository: MediaRepository
    private var dataTask: Task<(), any Error>?
    
    init() {
        dataProvider = KoinExtension.shared.homeAppBarUiDataProvider()
        mediaRepository = KoinExtension.shared.mediaRepository()
        
        setupDataStream()
    }
    
    private func setupDataStream() {
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.appBarAsyncSequence() else { return }
            
            do {
                for try await state in stream {
                    self?.uiState = state
                }
            } catch {
                // TODO: Handle error state
                print("Error in HomeViewModel data stream: \(error)")
            }
        }
    }
    
    func setContentMode(mode: MediaContentMode) {
        Task {
            try await mediaRepository.setContentMode(mode: mode)
        }
    }
    
    deinit {
        dataTask?.cancel()
    }
}

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    @State private var selectedTab = TopLevelNavigation.discover
    @EnvironmentObject var router: Router
    
    private var isAnimeBinding: Binding<Bool> {
        Binding<Bool>(
            get: { viewModel.uiState.contentMode == .anime },
            set: { viewModel.setContentMode(mode: $0 ? .anime : .manga) }
        )
    }
    
    var body: some View {
        let title = selectedTab == .discover ? "Discover" : "Track"
        TabView(selection: $selectedTab) {
            DiscoverView()
                .tabItem {
                    Label(
                        TopLevelNavigation.discover.label,
                        systemImage: selectedTab == .discover ?
                            TopLevelNavigation.discover.selectedIcon :
                            TopLevelNavigation.discover.unselectedIcon
                    )
                }
                .tag(TopLevelNavigation.discover)
            
            TrackView()
                .tabItem {
                    Label(
                        TopLevelNavigation.track.label,
                        systemImage: selectedTab == .track ?
                            TopLevelNavigation.track.selectedIcon :
                            TopLevelNavigation.track.unselectedIcon
                    )
                }
                .tag(TopLevelNavigation.track)
        }
        .navigationTitle(title)
        .toolbar {
            ToolbarItem(placement: .principal) {
                ModeSwitchView(isAnime: isAnimeBinding)
                    .accessibilityHint("Switch between anime and manga content")
            }
            
            ToolbarItem(placement: .topBarTrailing) {
                AvatarButton(
                    avatarUrl: viewModel.uiState.authedUser?.avatar,
                    action: { router.showAuthDialog() }
                )
            }
        }
        .animation(.easeInOut, value: selectedTab)
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
