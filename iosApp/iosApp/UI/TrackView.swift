import Shared
import SwiftUI

@MainActor
class TrackViewModel : ObservableObject {
    private let dataProvider: TrackUiDataProvider
    @Published var uiState: TrackUiState = TrackUiState.companion.Empty
    private var dataTask : Task<(), any Error>? = nil

    init() {
        print("TrackViewModel init")
        dataProvider = KoinHelper.shared.trackDataProvider()
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.gettrackUiStateAsyncSequence() else { return }
               
            for try await state in stream {
                self?.uiState = state
            }
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
