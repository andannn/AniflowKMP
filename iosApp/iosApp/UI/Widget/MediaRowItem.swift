import SwiftUI

struct MediaRowSimple: View {
    let title: String
    let subtitle: String?
    let imageURL: URL?
    var canMarkWatched: Bool = true
    var onClick: () -> Void = {}
    var onDelete: () -> Void = {}
    var onMarkWatched: () -> Void = {}

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            AsyncImage(url: imageURL) { img in
                img.resizable().scaledToFill()
            } placeholder: {
                Color.secondary.opacity(0.2)
            }
            .frame(width: 85, height: 120)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))

            VStack(alignment: .leading, spacing: 10) {
                Text(title)
                    .font(.title3.weight(.semibold))
                    .foregroundStyle(.secondary)
                    .lineLimit(2)

                if let subtitle {
                    Text(subtitle)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }

                Spacer(minLength: 0)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .contentShape(Rectangle())
        .onTapGesture { onClick() }
        // 右滑动作（leading）
        .swipeActions(edge: .leading, allowsFullSwipe: true) {
            if canMarkWatched {
                Button {
                    onMarkWatched()
                } label: {
                    Label("Mark watched", systemImage: "bookmark")
                }
                .tint(.indigo)
            }
        }
        // 左滑动作（trailing）
        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
            Button(role: .destructive) {
                onDelete()
            } label: {
                Label("Delete", systemImage: "trash")
            }
        }
    }
}
