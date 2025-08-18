import Shared
import SwiftUI

@MainActor
class TrackViewModel : ObservableObject {
    private let dataProvider: DataProviderWrapper
    @Published var uiState: TrackUiState = TrackUiState.companion.Empty
    
    init() {
        print("TrackViewModel init")
        dataProvider = DataProviderWrapper(ktDataProvider: KoinHelper.shared.dataProvider())
        Task {
            do {
                for try await state in dataProvider.gettrackUiStateAsyncSequence() {
                    uiState = state
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    deinit {
        print("TrackViewModel deinit")

    }
}


struct TrackView: View {
    @StateObject
    private var viewModel: TrackViewModel = TrackViewModel()

    var body: some View {
        TrackContent(
            viewModel.uiState.items
        )
    }
}

struct TrackContent: View {
    private let items: [MediaWithMediaListItem]

    init(_ items:  [MediaWithMediaListItem]) {
        self.items = items
    }

    var body: some View {
        List(items, id: \.self.mediaListModel.id) { item in
            Text(item.mediaModel.title?.romaji ?? "value")
        }
    }
}
