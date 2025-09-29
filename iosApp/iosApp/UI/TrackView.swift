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
    let snackbarManager = SnackbarManager()

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
    
    func onMarkWatched(item: MediaWithMediaListItem) {
        print("TrackViewModel onMarkWatched click E")
        Task {
            let newProgress = Int(truncating: item.mediaListModel.progress ?? 0) + 1
 
            try await MarkProgressUseCase.shared.markProgress(
                item: item,
                newProgress: Int32(newProgress),
                snackBarMessageHandler: SnackbarMessageHandlerImpl(snackbarManager: snackbarManager),
                errorHandler: errorChannel
            )
        }
    }
    
    func onDelete(item: MediaWithMediaListItem) {
        print("TrackViewModel onDelete click")
        Task {
 
            try await MarkProgressUseCase.shared.markDropped(
                item: item,
                snackBarMessageHandler: SnackbarMessageHandlerImpl(snackbarManager: snackbarManager),
                errorHandler: errorChannel
            )
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
        .snackbar(manager: viewModel.snackbarManager)
        .errorHandling(source: viewModel.errorChannel, snackbarManager: viewModel.snackbarManager)
    }
}

struct TrackSectionView: View {
    let group: TrackUiState.CategoryWithItems
    let onDelete: (MediaWithMediaListItem) -> Void
    let onMarkWatched: (MediaWithMediaListItem) -> Void
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
    let onDelete: (MediaWithMediaListItem) -> Void
    let onMarkWatched: (MediaWithMediaListItem) -> Void
    let onClick: (MediaWithMediaListItem) -> Void
    
    var body: some View {
        MediaRowSimple(
            item: item,
            userTitleLanguage: UserTitleLanguage.english,
            onClick: { onClick(item) },
            onDelete: { onDelete(item) },
            onMarkWatched: { onMarkWatched(item) }
        )
        .padding(.vertical, 4)
        .padding(.horizontal, 4)
        .background(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(Color(.secondarySystemGroupedBackground))
                .shadow(color: Color.black.opacity(0.06), radius: 3, x: 0, y: 2)
        )
        .listRowSeparator(.hidden)
        .listRowBackground(Color.clear)
    }
}
