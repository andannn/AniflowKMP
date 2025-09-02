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
            state: viewModel.uiState
        )
    }
}

struct TrackContent: View {
    let state: TrackUiState
    
    var body: some View {
        VStack {
            
        }
        //        List(items, id: \.self.mediaListModel.id) { item in
        //            Text(item.mediaModel.title?.romaji ?? "value")
        //        }
    }
}
//
//struct MediaListView: View {
//    let categoryWithItemsList: [TrackUiState.CategoryWithItems]
//    let onClickListItem: (MediaModel) -> Void
//    let onDeleteItem: (MediaListModel) -> Void
//    let onMarkWatched: (MediaListModel) -> Void
//    
//    var body: some View {
//        LazyVStack {
//            ForEach(categoryWithItemsList, id: \.self.category) { group in
//                ItemGroupSection(
//                    group: group
//                )
//            }
//        }
//    }
//}
//
//private struct ItemGroupSection: View {
//    let group : TrackUiState.CategoryWithItems
//    let onClickListItem: () -> Void = {}
//    let onDeleteItem: () -> Void = {}
//    let onMarkWatched: () -> Void = {}
//    
//    var body: some View {
//        Section {
//            ForEach(Array(group.items), id: \.self.mediaListModel.id) { item in
//                let index = group.items.lastIndex(of: item)
//                let isFirst = index == 0
//                let isLast  = index == group.items.count - 1
//                MediaRowSimple(
//                    
//                   
////                    canMarkWatched: item.haveNextEpisode,
////                    onClick: { onClickListItem(item.mediaModel) },
////                    onDelete: { onDeleteItem(item.mediaListModel) },
////                    onMarkWatched: { onMarkWatched(item.mediaListModel) }
//                )
////                .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
////                .listRowSeparator(.hidden)
////                .modifier(ListItemCornerModifier(isFirst: isFirst, isLast: isLast))
//            }
//        } header: {
//            // stickyHeader 等价：Section 的 header 视图
//            Text("")
//            //                    ZStack(alignment: .leading) {
//            //                        // surfaceContainerHigh 的效果可用系统材质或指定颜色
//            //                        // 这里简单用 .secondarySystemBackground 近似
//            //                        Color(uiColor: .secondarySystemBackground)
//            //                        Text(group.title)
//            //                            .font(.labelLarge) // 自定义扩展，或用 .callout / .subheadline 等
//            //                            .padding(.top, 12)
//            //                            .padding(.leading, 18)
//            //                            .padding(.bottom, 6)
//            //                    }
//            //                    // 让 header 也占满宽度
//            //                    .frame(maxWidth: .infinity, alignment: .leading)
//        }
//        .textCase(nil)
//    }
//    
//    private func buildCenterText(_ item: MediaWithMediaListItem) -> String? {
//        if item.haveNextEpisode {
//            // 例：Next up: Episode X
//            return "Next up: Episode …" // 根据你的进度字段拼接
//        } else if item.hasReleaseInfo {
//            // 例：Episode 10 in 10d
//            return "Episode … in …"     // 按你的 releasingTimeString() 拼接
//        } else {
//            return "No upcoming episode"
//        }
//    }
//    
//    private func buildInfoText(_ m: MediaModel) -> String? {
//        // 复刻你的 infoString() 逻辑，返回 "TV · 2024 · Summer · 12 Ep · Releasing"
//        // 这里留空由你代入
//        return nil
//    }
//}
//
//
//// MARK: - 首/末项圆角修饰
//private struct ListItemCornerModifier: ViewModifier {
//    var isFirst: Bool
//    var isLast: Bool
//    
//    func body(content: Content) -> some View {
//        Group {
//            if #available(iOS 17.0, *) {
//                content
//                    .clipShape(
//                        UnevenRoundedRectangle(
//                            topLeadingRadius: isFirst ? 12 : 0,
//                            bottomLeadingRadius: isLast ? 12 : 0,
//                            bottomTrailingRadius: isLast ? 12 : 0,
//                            topTrailingRadius: isFirst ? 12 : 0
//                        )
//                    )
//                    .contentShape(Rectangle())
//            } else {
//                content
//                    .clipShape(
//                        RoundedCorners(
//                            radius: 12,
//                            corners: corners()
//                        )
//                    )
//                    .contentShape(Rectangle())
//            }
//        }
//    }
//    
//    private func corners() -> UIRectCorner {
//        switch (isFirst, isLast) {
//        case (true, false):  return [.topLeft, .topRight]
//        case (false, true):  return [.bottomLeft, .bottomRight]
//        case (true, true):   return [.allCorners]
//        default:             return []
//        }
//    }
//}
//
//struct RoundedCorners: Shape {
//    var radius: CGFloat = 12
//    var corners: UIRectCorner = []
//    func path(in rect: CGRect) -> Path {
//        let path = UIBezierPath(
//            roundedRect: rect,
//            byRoundingCorners: corners,
//            cornerRadii: CGSize(width: radius, height: radius)
//        )
//        return Path(path.cgPath)
//    }
//}
