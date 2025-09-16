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
            let error = try await mediaRepository.updateMediaListProgress(mediaListId: item.id, progress: current + 1
            )
            print("TrackViewModel onMarkWatched click X \(String(describing: error))")
        }
    }
    
    func onDelete(item: MediaListModel) {
        print("TrackViewModel onDelete click")
        Task {
           try await mediaRepository.updateMediaStatus(mediaListId: item.id, status: .dropped)
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

    var body: some View {
        let categoryWithItemsList = viewModel.uiState.categoryWithItems
        List {
            ForEach(categoryWithItemsList, id: \.self.category) { group in
                if !group.items.isEmpty {
                    Section(header: Text(group.category.title)) {
                        ForEach(group.items.indices, id: \.self) { index in
                            MediaRowSimple(
                                item: group.items[index],
                                userTitleLanguage: UserTitleLanguage.english,
                                onDelete: {
                                    viewModel.onDelete(item: group.items[index].mediaListModel)
                                },
                                onMarkWatched: {
                                    viewModel.onMarkWatched(
                                        item: group.items[index].mediaListModel
                                    )
                                }
                            )
                            .listRowInsets(EdgeInsets(top: 6, leading: 6, bottom: 6, trailing: 6))
                        }
                    }
                }
            }
        }
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
