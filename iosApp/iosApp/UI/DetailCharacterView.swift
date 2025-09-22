
import SwiftUI
import Shared

@MainActor
class DetailCharacterViewModel: ObservableObject {
    let characterId: String
    @Published public var uiState = DetailCharacterUiState.companion.Empty
    
    private let dataProvider: DetailCharacterUiDataProvider
    
    private var dataTask:  Task<(), any Error>? = nil
    private var sideEffectTask:  Task<(), any Error>? = nil
    
    init(characterId: String) {
        self.characterId = characterId
        dataProvider = KoinHelper.shared.detailCharacterUiDataProvider(characterId: characterId)
        
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.uiDataFlowAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
        sideEffectTask = Task { [weak self] in
            guard let stream = self?.dataProvider.uiSideEffectAsyncSequence(true) else { return }
            for try await _ in stream {
                //                self?.uiState = state
            }
        }
    }
    
    
    deinit {
        print("DetailCharacterViewModel deinit")
        dataTask?.cancel()
        sideEffectTask?.cancel()
    }
}

struct DetailCharacterView: View {
    let characterId: String
    @StateObject private var viewModel: DetailCharacterViewModel
    
    init(characterId: String) {
        self.characterId = characterId
        _viewModel = StateObject(wrappedValue: DetailCharacterViewModel(characterId: characterId))
    }
    
    var body: some View {
        let uiState = viewModel.uiState
        
        DetailCharacterContentView(
            character: uiState.characterModel,
            options: uiState.userOption,
            //            selectedMediaSort: uiState.selectedMediaSort,
            onBack: { /* Handle back action */ },
            onSelectMediaSort: { _ in /* Handle media sort selection */ },
            onMediaClick: { _ in /* Handle media click */ },
            onToggleFavoriteClick: { /* Handle toggle favorite */ },
            loadNextPage: { /* Handle load next page */ }
        )
        
    }
}


struct DetailCharacterContentView: View {
    let character: CharacterModel?
    let options: UserOptions
    //    let selectedMediaSort: MediaSort
    let onBack: () -> Void
    let onSelectMediaSort: (MediaSort) -> Void
    let onMediaClick: (MediaModel) -> Void
    let onToggleFavoriteClick: () -> Void
    let loadNextPage: () -> Void
    
    @State private var expanded = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Centered Image
                if let imageUrl = character?.image {
                    HStack {
                        Spacer()
                        AsyncImage(url: URL(string: imageUrl)) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                        } placeholder: {
                            Color.gray
                        }
                        .frame(width: 160, height: 220)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        Spacer()
                    }
                }
                
                // Details
                VStack(alignment: .leading, spacing: 8) {
                    if let character = character {
                        //                            if let birthday = character.dateOfBirth {
                        //                                Text("Birthday: \(birthday.format())")
                        //                            }
                        if let age = character.age {
                            Text("Age: \(age)")
                        }
                        if let gender = character.gender {
                            Text("Gender: \(gender)")
                        }
                        if let bloodType = character.bloodType {
                            Text("Blood Type: \(bloodType)")
                        }
                        //                            if let description = character.description {
                        //                                Text(description)
                        //                            }
                    }
                }
                .font(.system(size: 14))
                .padding(.horizontal)
            }
        }
    }
}

