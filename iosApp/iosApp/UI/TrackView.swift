import Shared
import SwiftUI

@MainActor
class TrackViewModel : ObservableObject {
    private let dataProvider: TrackUiDataProvider = KoinHelper.shared.trackDataProvider()
    private let mediaRepository = KoinHelper.shared.mediaRepository()
    
    @Published var uiState: TrackUiState = TrackUiState.companion.Empty
    
    private var dataTask : Task<(), any Error>? = nil
    private var refreshCompleter: OneShotCompleter<Void>? = nil
    private var sideEffectTask: Task<Void, Never>? = nil
    
    let errorChannel: ErrorChannel = AppErrorKt.buildErrorChannel()
    
    init() {
        print("TrackViewModel init")
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.gettrackUiStateAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
        cancelLastAndRegisterUiSideEffect(force: false, completer: nil)
    }
    
    
    func doRefreshAndAwait() async throws {
        refreshCompleter?.cancel()
        refreshCompleter = OneShotCompleter<Void>()
        cancelLastAndRegisterUiSideEffect(force: true, completer: refreshCompleter)
        try await refreshCompleter?.wait()
    }
    
    func cancelLastAndRegisterUiSideEffect(force: Bool, completer: OneShotCompleter<Void>?) {
        sideEffectTask?.cancel()
        var isCompleted = false
        sideEffectTask = Task { [weak self] in
            guard let stream = self?.dataProvider.trackUiSideEffectErrorSequence(force) else { return }
            
            do {
                for try await status in stream {
                    guard let self = self else { continue }
                    
                    AppErrorKt.submitErrorOfSyncStatus(self.errorChannel, status: status)
                    
                    // handle side effect status.
                    print("TrackViewModel cancelLastAndRegisterUiSideEffect status: \(status)")
                    if completer != nil && !status.isLoading() && !isCompleted {
                        print("TrackViewModel cancelLastAndRegisterUiSideEffect refresh completed")
                        completer?.complete(())
                        isCompleted = true
                    }
                }
            } catch is CancellationError {
                print("TrackViewModel cancelLastAndRegisterUiSideEffect Canceled.")
            } catch {
                print("TrackViewModel cancelLastAndRegisterUiSideEffect Failed with error: \(error)")
            }
        }
    }
    
    func onMarkWatched(item: MediaListModel) {
        print("TrackViewModel onMarkWatched click E")
        Task {
            let current = Int(truncating: item.progress ?? 0)
            let error = try await mediaRepository.updateMediaListStatus(
                mediaListId: item.id,
                progress: current + 1
            )
            print("TrackViewModel onMarkWatched click X \(String(describing: error))")
        }
    }
    
    func onDelete(item: MediaListModel) {
        print("TrackViewModel onDelete click")
        Task {
            try await mediaRepository.updateMediaListStatus(mediaListId: item.id,status: .dropped)
        }
    }
    
    deinit {
        print("TrackViewModel deinit")
        dataTask?.cancel()
    }
}


struct TrackView: View {
    @StateObject
    private var viewModel: TrackViewModel = TrackViewModel()
    @StateObject private var snackbarManager = SnackbarManager()
    @EnvironmentObject private var router: Router
    
    var body: some View {
        let categoryWithItemsList = viewModel.uiState.categoryWithItems
        List {
            ForEach(categoryWithItemsList, id: \.category) { group in
                if !group.items.isEmpty {
                    TrackSectionView(
                        group: group,
                        onDelete: { item in viewModel.onDelete(item: item) },
                        onMarkWatched: { item in viewModel.onMarkWatched(item: item) },
                        onClick: { item in
                            router.navigateTo(route: .detailMedia(mediaId: item.mediaModel.id))
                        }
                    )
                }
            }
        }
        .listStyle(.insetGrouped)
        .contentMargins(.horizontal, 10, for: .scrollContent)
        .refreshable {
            do {
                try await viewModel.doRefreshAndAwait()
            } catch {
                print("Track error when refresh \(error)")
            }
        }
        .snackbar(manager: snackbarManager)
        .errorHandling(source: viewModel.errorChannel, snackbarManager: snackbarManager)
    }
}

struct TrackSectionView: View {
    let group: TrackUiState.CategoryWithItems
    let onDelete: (MediaListModel) -> Void
    let onMarkWatched: (MediaListModel) -> Void
    let onClick: (MediaWithMediaListItem) -> Void
    
    var body: some View {
        Section(header:
            Text(group.category.title)
                .font(.headline)
                .foregroundColor(.primary)
                .padding(.top, 12)
                .padding(.bottom, 4)
        ) {
            ForEach(group.items, id: \.mediaListModel.id) { item in
                TrackRowView(
                    item: item,
                    onDelete: onDelete,
                    onMarkWatched: onMarkWatched,
                    onClick: onClick
                )
            }
        }
    }
}

struct TrackRowView: View {
    let item: MediaWithMediaListItem
    let onDelete: (MediaListModel) -> Void
    let onMarkWatched: (MediaListModel) -> Void
    let onClick: (MediaWithMediaListItem) -> Void
    
    var body: some View {
        MediaRowSimple(
            item: item,
            userTitleLanguage: UserTitleLanguage.english,
            onClick: { onClick(item) },
            onDelete: { onDelete(item.mediaListModel) },
            onMarkWatched: { onMarkWatched(item.mediaListModel) }
        )
        .padding(.vertical, 8)
        .padding(.horizontal, 4)
        .background(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(Color(.secondarySystemGroupedBackground))
                .shadow(color: Color.black.opacity(0.06), radius: 3, x: 0, y: 2)
        )
        .listRowSeparator(.hidden)
        .listRowBackground(Color.clear)
        .accessibilityElement(children: .combine)
//        .accessibilityLabel(item.mediaListModel.media?.title?.getUserTitleString(titleLanguage: .english) ?? "")
    }
}
