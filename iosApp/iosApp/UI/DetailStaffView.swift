import SwiftUI
import Shared
import Combine

@MainActor
class DetailStaffViewModel: ObservableObject {
    let staffId: String
    @Published public var uiState = DetailStaffUiState.companion.Empty
    
    @Published  var pageComponent: StaffCharactersPageComponent
    @Published public var mediaSort = MediaSort.startDateDesc
    
    private let dataProvider: DetailStaffUiDataProvider
    
    private var dataTask:  Task<(), any Error>? = nil
    private var sideEffectTask:  Task<(), any Error>? = nil
    private var cancellables = Set<AnyCancellable>()
    
    init(staffId: String) {
        self.staffId = staffId
        dataProvider = KoinHelper.shared.detailStaffUiDataProvider(staffId: staffId)
        pageComponent = PageComponentFactory.shared.createStaffCharactersPaging(staffId: staffId, sort: MediaSort.startDateDesc)
        
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
        $mediaSort
            .removeDuplicates()
            .sink { [weak self] sort in
                guard let self else { return }
                self.pageComponent.dispose()
                self.pageComponent = PageComponentFactory.shared.createStaffCharactersPaging(staffId: staffId, sort: sort)
            }
            .store(in: &cancellables)
    }
    
    
    deinit {
        print("DetailStaffViewModel deinit")
        dataTask?.cancel()
        sideEffectTask?.cancel()
        cancellables.forEach { cancellable in
            cancellable.cancel()
        }
    }
}

struct DetailStaffView: View {
    let staffId: String
    @StateObject private var viewModel: DetailStaffViewModel
    @EnvironmentObject private var router: Router
    init(staffId: String) {
        self.staffId = staffId
        _viewModel = StateObject(wrappedValue: DetailStaffViewModel(staffId: staffId))
    }
    
    var body: some View {
        let uiState = viewModel.uiState
        
        DetailStaffContentView(
            staff: uiState.staffModel,
            options: uiState.userOption,
            pageComponent: viewModel.pageComponent,
            selectedMediaSort: viewModel.mediaSort,
            onSelectMediaSort: { sort in
                viewModel.mediaSort = sort
            },
            onClickCharacter: { model in
                router.navigateTo(route: .detailCharacter(characterId: model.id))
            },
            onClickMedia: { model in
                router.navigateTo(route: .detailMedia(mediaId: model.id))
            }
        )
        .navigationTitle(uiState.title)
    }
}


struct DetailStaffContentView: View {
    let staff: StaffModel?
    let options: UserOptions
    let pageComponent: StaffCharactersPageComponent
    let selectedMediaSort: MediaSort
    var onSelectMediaSort: (MediaSort) -> Void = {_ in}
    var onMediaClick: (MediaModel) -> Void = {_ in}
    var onToggleFavoriteClick: () -> Void = {}
    var loadNextPage: () -> Void = {}
    var onClickCharacter: (CharacterModel) -> Void = {_ in }
    var onClickMedia: (MediaModel) -> Void = {_ in }
    
    @State private var expanded = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Centered Image
                if let imageUrl = staff?.image {
                    HStack {
                        Spacer()
                        CustomAsyncImage(url: imageUrl, contentMode: .fill)
                            .frame(width: 160, height: 220)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                        Spacer()
                    }
                }
                
                // Details
                VStack(alignment: .leading, spacing: 8) {
                    if let staff = staff {
                        if let birthday = staff.dateOfBirth {
                            Text("Birthday: \(birthday.format())")
                        }
                        if let age = staff.age {
                            Text("Age: \(age)")
                        }
                        if let gender = staff.gender {
                            Text("Gender: \(gender)")
                        }
                        if let bloodType = staff.bloodType {
                            Text("Blood Type: \(bloodType)")
                        }
                        if let description = staff.description_ {
                            HTMLText(html: description)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                }
                .font(.system(size: 14))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal)
            
            HStack {
                Spacer()
                MediaSortFilterMenu(
                    selectedCategory: selectedMediaSort,
                    onSelectCategory: onSelectMediaSort
                )
            }
            .padding(.horizontal)
            
            let columns = [GridItem(.adaptive(minimum: 120), spacing: 8)]
            VerticalGridPaging<VoicedCharacterWithMedia, _>(
                pageComponent: pageComponent,
                columns: columns,
                contentPadding: .init(top: 0, leading: 16, bottom: 0, trailing: 16),
                key: { AnyHashable($0) },
                itemContent: { item in
                    CharacterWithMediaItem(
                        item: item,
                        userTitleLanguage: options.titleLanguage,
                        userStaffLanguage: options.staffNameLanguage,
                        onCharacterClick: {
                            onClickCharacter(item.character)
                        },
                        onMediaClick: {
                            onClickMedia(item.media)
                        }
                    )
                }
            ).id(selectedMediaSort)

        }
    }
}
