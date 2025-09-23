import SwiftUI
import Shared

@MainActor
class DetailMediaStaffPagingViewModel: ObservableObject {
    let mediaId: String
    let pageComponent: DetailMediaStaffPageComponent
    
    init(mediaId: String) {
        self.mediaId = mediaId
        pageComponent = PageComponentFactory.shared.createDetailMediaStaffPaging(mediaId: mediaId)
    }
    
    deinit {
        pageComponent.dispose()
    }
}


struct DetailMediaStaffPaging: View {
    let mediaId: String
    @StateObject private var viewModel: DetailMediaStaffPagingViewModel
    @EnvironmentObject private var router: Router

    init(mediaId: String) {
        self.mediaId = mediaId
        
        _viewModel = StateObject(wrappedValue: DetailMediaStaffPagingViewModel(mediaId: mediaId))
    }
    
    var body: some View {
        VerticalListPaging<StaffWithRole, _>(
            pageComponent: viewModel.pageComponent,
            contentPadding: .init(top: 0, leading: 16, bottom: 0, trailing: 16),
            key: { item in AnyHashable(item) },
            itemContent: { item in
                StaffRowItem(
                    staffWithRole: item,
                    userStaffLanguage: .native,
                    onClick: {
                        router.navigateTo(route: .detailStaff(staffId: item.staff.id))
                    }
                )
            }
        )
        .navigationTitle("Characters")
    }
}
