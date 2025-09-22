
import SwiftUI
import Shared

@MainActor
class DetailStaffViewModel: ObservableObject {
    let staffId: String
    @Published public var uiState = DetailStaffUiState.companion.Empty
    
    private let dataProvider: DetailStaffUiDataProvider
    
    private var dataTask:  Task<(), any Error>? = nil
    private var sideEffectTask:  Task<(), any Error>? = nil
    
    init(staffId: String) {
        self.staffId = staffId
        dataProvider = KoinHelper.shared.detailStaffUiDataProvider(staffId: staffId)
        
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.uiStateAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
        sideEffectTask = Task { [weak self] in
            guard let stream = self?.dataProvider.uiSideEffectStatusSequence(true) else { return }
            for try await _ in stream {
                //                self?.uiState = state
            }
        }
    }
    
    
    deinit {
        print("DetailStaffViewModel deinit")
        dataTask?.cancel()
        sideEffectTask?.cancel()
    }
}

struct DetailStaffView: View {
    let staffId: String
    @StateObject private var viewModel: DetailStaffViewModel
    
    init(staffId: String) {
        self.staffId = staffId
        _viewModel = StateObject(wrappedValue: DetailStaffViewModel(staffId: staffId))
    }
    
    var body: some View {
        let uiState = viewModel.uiState
        
        DetailStaffContentView(
            staff: uiState.staffModel,
            options: uiState.userOption,
            //            selectedMediaSort: uiState.selectedMediaSort,
            onBack: { /* Handle back action */ }
        )
    }
}


struct DetailStaffContentView: View {
    let staff: StaffModel?
    let options: UserOptions
    //    let selectedMediaSort: MediaSort
    var onBack: () -> Void = {}
    var onSelectMediaSort: (MediaSort) -> Void = {_ in}
    var onMediaClick: (MediaModel) -> Void = {_ in}
    var onToggleFavoriteClick: () -> Void = {}
    var loadNextPage: () -> Void = {}
    
    @State private var expanded = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Centered Image
                if let imageUrl = staff?.image {
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
                    if let staff = staff {
                        //                            if let birthday = character.dateOfBirth {
                        //                                Text("Birthday: \(birthday.format())")
                        //                            }
                        if let age = staff.age {
                            Text("Age: \(age)")
                        }
                        if let gender = staff.gender {
                            Text("Gender: \(gender)")
                        }
                        if let bloodType = staff.bloodType {
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

