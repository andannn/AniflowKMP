import SwiftUI
import Shared

@MainActor
class DetailMediaCharacterPagingViewModel: ObservableObject {
    let mediaId: String
    let pageComponent: DetailMediaCharacterPageComponent
    
    init(mediaId: String) {
        self.mediaId = mediaId
        pageComponent = PageComponentFactory.shared.createDetailMediaCharacterPaging(mediaId: mediaId)
    }
    
    deinit {
        pageComponent.dispose()
    }
}

struct DetailMediaCharacterPaging: View {
    let mediaId: String
    @StateObject private var viewModel: DetailMediaCharacterPagingViewModel
    @EnvironmentObject private var router: Router
    
    init(mediaId: String) {
        self.mediaId = mediaId
        
        _viewModel = StateObject(wrappedValue: DetailMediaCharacterPagingViewModel(mediaId: mediaId))
    }
    
    var body: some View {
        VerticalListPaging<CharacterWithVoiceActor, _>(
            pageComponent: viewModel.pageComponent,
            contentPadding: .init(top: 0, leading: 16, bottom: 0, trailing: 16),
            key: { item in AnyHashable(item.character.id) },
            itemContent: { item in
                CharacterRowItem(
                    characterWithVoiceActor: item,
                    userStaffLanguage: .native,
                    onStaffClick: { staff in
                        router.navigateTo(route: .detailStaff(staffId: staff.id))
                    },
                    onCharacterClick: { _ in
                        router.navigateTo(route: .detailCharacter(characterId: item.character.id))
                    }
                )
            }
        )
        .navigationTitle("Characters")
    }
}
