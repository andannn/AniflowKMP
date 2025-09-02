import Shared
import SwiftUI

struct BannerImageView: View {
    let url: URL?
    let corner: CGFloat
    let height: CGFloat

    var body: some View {
        AsyncImage(url: url) { phase in
            switch phase {
            case .empty:
                ZStack {
                    Rectangle().fill(.secondary.opacity(0.08))
                    ProgressView()
                }
            case .success(let image):
                image
                    .resizable()
                    .scaledToFill()
            case .failure:
                ZStack {
                    Rectangle().fill(.secondary.opacity(0.08))
                    Image(systemName: "photo")
                        .imageScale(.large)
                        .foregroundStyle(.secondary)
                }
            @unknown default:
                Rectangle().fill(.secondary.opacity(0.08))
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: height)
        .clipped()
        .clipShape(RoundedRectangle(cornerRadius: corner, style: .continuous))
        .contentShape(Rectangle())
    }
}

@available(iOS 17.0, *)
struct CarouselItemView: View {
    let index: Int
    let item: MediaWithMediaListItem
    let corner: CGFloat
    let itemHeight: CGFloat
    let onTap: (Int) -> Void

    var body: some View {
        let urlString = item.mediaModel.bannerImage ?? item.mediaModel.coverImage
        let url = urlString.flatMap { URL(string: $0) }

        BannerImageView(url: url, corner: corner, height: itemHeight)
            .id(index)
            .containerRelativeFrame(.horizontal)
            .scrollTransition(.interactive, axis: .horizontal) { content, phase in
                content
                    .scaleEffect(phase.isIdentity ? 1.0 : 0.94)
                    .opacity(phase.isIdentity ? 1.0 : 0.85)
            }
            .onTapGesture { onTap(index) }
    }
}

@available(iOS 17.0, *)
struct NewReleaseCard_iOS17: View {
    let items: [MediaWithMediaListItem]
    let userTitleLanguage: UserTitleLanguage

    @State private var currentId: Int? = 0

    // 提前把常量抽出来，避免字面量+链式调用堆到一个表达式里
    private let corner: CGFloat = 24
    private let itemSpacing: CGFloat = 8
    private let itemHeight: CGFloat = 110

    var body: some View {
        // 外层卡片尽量简单
        VStack(alignment: .leading, spacing: 12) {
            header
            if !items.isEmpty { carousel }
            titleRow
            metaRow
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 12)
        .background(RoundedRectangle(cornerRadius: corner, style: .continuous)
            .fill(Color(.secondarySystemBackground)))
        .overlay(RoundedRectangle(cornerRadius: corner, style: .continuous)
            .stroke(Color(.separator), lineWidth: 1))
        .clipShape(RoundedRectangle(cornerRadius: corner, style: .continuous))
        .onAppear { if currentId == nil { currentId = 0 } }
    }

    private var header: some View {
        HStack {
            Text("New Release")
                .font(.title3.weight(.semibold))
                .foregroundStyle(.tertiary)
            Spacer()
        }
        .padding(.top, 2)
    }

    private var carousel: some View {
        ScrollView(.horizontal) {
            LazyHStack(spacing: itemSpacing) {
                ForEach(items.indices, id: \.self) { i in
                    CarouselItemView(
                        index: i,
                        item: items[i],
                        corner: corner,
                        itemHeight: itemHeight,
                        onTap: { tapped in
                            withAnimation(.spring(response: 0.35, dampingFraction: 0.9)) {
                                currentId = tapped
                            }
                        }
                    )
                }
            }
            .scrollTargetLayout()
        }
        .scrollTargetBehavior(.paging)
        .scrollIndicators(.hidden)
        .scrollPosition(id: $currentId)
        .frame(height: itemHeight)
        .padding(.horizontal, 6)
        .overlay(pageDots, alignment: .bottomLeading)
    }

    private var pageDots: some View {
        HStack(spacing: 6) {
            ForEach(items.indices, id: \.self) { i in
                Circle()
                    .frame(width: 8, height: 8)
                    .opacity((currentId ?? 0) == i ? 1 : 0.3)
                    .onTapGesture {
                        withAnimation(.easeInOut(duration: 0.25)) {
                            currentId = i
                        }
                    }
            }
        }
        .padding(.leading, 8)
        .padding(.bottom, 8)
    }

    private var titleRow: some View {
        Group {
            if let i = currentId,
               items.indices.contains(i),
               let title = items[i].mediaModel.title?.getUserTitleString(titleLanguage: userTitleLanguage) {
                Text(title)
                    .font(.title2.weight(.semibold))
                    .lineLimit(2)
            } else {
                EmptyView()
            }
        }
    }

    private var metaRow: some View {
        HStack {
            if let i = currentId, items.indices.contains(i) {
                let next = (items[i].mediaListModel.progress?.intValue ?? 0) + 1
                Text("Next episode in ")
                    .font(.system(size: 18))
                + Text("\(next)")
                    .font(.system(size: 30, weight: .semibold))
                    .foregroundStyle(.tint)
            } else {
                Text("Next episode in –")
                    .font(.system(size: 18))
            }
            Spacer()
            Button("Detail") {
                // TODO: action
            }
            .buttonStyle(.borderless)
        }
    }
}
