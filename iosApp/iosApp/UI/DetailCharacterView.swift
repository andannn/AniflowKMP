import SwiftUI
import Shared
import Foundation
import UIKit
import Combine

@MainActor
class DetailCharacterViewModel: ObservableObject {
    let characterId: String
    @Published public var uiState = DetailCharacterUiState.companion.Empty
    @Published public var mediaSort = MediaSort.startDateDesc
    @Published public var pagingComponent: CharacterDetailMediaPaging
    
    private let dataProvider: DetailCharacterUiDataProvider
    
    private var dataTask:  Task<(), any Error>? = nil
    private var sideEffectTask:  Task<(), any Error>? = nil
    private var cancellables = Set<AnyCancellable>()

    init(characterId: String) {
        self.characterId = characterId
        dataProvider = KoinHelper.shared.detailCharacterUiDataProvider(characterId: characterId)
        pagingComponent = PageComponentFactory.shared.characterDetailMediaPaging(characterId: characterId, sort: MediaSort.startDateDesc)

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
        
        $mediaSort
            .removeDuplicates()
            .sink { [weak self] sort in
                guard let self else { return }
                
                self.pagingComponent.dispose()
                self.pagingComponent = PageComponentFactory.shared.characterDetailMediaPaging(characterId: characterId, sort: sort)
            }
            .store(in: &cancellables)
    }
    
    
    deinit {
        print("DetailCharacterViewModel deinit")
        dataTask?.cancel()
        sideEffectTask?.cancel()
        cancellables.forEach { cancellable in
            cancellable.cancel()
        }
        cancellables.removeAll()
    }
}

struct DetailCharacterView: View {
    let characterId: String
    @StateObject private var viewModel: DetailCharacterViewModel
    @EnvironmentObject private var router: Router
    
    init(characterId: String) {
        self.characterId = characterId
        _viewModel = StateObject(wrappedValue: DetailCharacterViewModel(characterId: characterId))
    }
    
    var body: some View {
        let uiState = viewModel.uiState
        
        DetailCharacterContentView(
            character: uiState.characterModel,
            options: uiState.userOption,
            pagingComponent: viewModel.pagingComponent,
            selectedMediaSort: viewModel.mediaSort,
            onMediaClick: { model in
                router.navigateTo(route: .detailMedia(mediaId: model.id))
            },
            onToggleFavoriteClick: { /* Handle toggle favorite */ },
            onSelectMediaSort: { sort in
                viewModel.mediaSort = sort
            }
        )
        .navigationTitle(uiState.title)
    }
}


struct DetailCharacterContentView: View {
    let character: CharacterModel?
    let options: UserOptions
    let pagingComponent: CharacterDetailMediaPaging
        let selectedMediaSort: MediaSort
    let onMediaClick: (MediaModel) -> Void
    let onToggleFavoriteClick: () -> Void
    let onSelectMediaSort: (MediaSort) -> Void
    
    @State private var expanded = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Centered Image
                if let imageUrl = character?.image {
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
                    if let character = character {
                        if let birthday = character.dateOfBirth {
                            Text("Birthday: \(birthday.format())")
                        }
                        if let age = character.age {
                            Text("Age: \(age)")
                        }
                        if let gender = character.gender {
                            Text("Gender: \(gender)")
                        }
                        if let bloodType = character.bloodType {
                            Text("Blood Type: \(bloodType)")
                        }
                        if let description = character.description_ {
                            HTMLText(html: description)
                        }
                    }
                }
                .font(.system(size: 14))
                .padding(.horizontal)
                
                HStack {
                    Spacer()
                    MediaSortFilterMenu(
                        selectedCategory: selectedMediaSort,
                        onSelectCategory: onSelectMediaSort
                    )
                }
                .padding(.horizontal)
              
                let cols = [GridItem(.adaptive(minimum: 120), spacing: 12)]

                VerticalGridPaging<MediaModel, _>(
                    pageComponent: pagingComponent,
                    columns: cols, 
                    contentPadding: .init(top: 0, leading: 16, bottom: 0, trailing: 16),
                    key: { AnyHashable($0.id) },
                    itemContent: { item in
                        let title = item.title?.getUserTitleString(titleLanguage: options.titleLanguage) ?? ""
                        MediaPreviewItem(title: title, coverImage: item.coverImage, onClick: { onMediaClick(item) } )
                    }
                )
                .id(selectedMediaSort)
            }
        }
    }
}
