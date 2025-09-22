import SwiftUI
import Shared

struct MediaRowSimple: View {
    let item: MediaWithMediaListItem
    let userTitleLanguage: UserTitleLanguage
    var onClick: () -> Void = {}
    var onDelete: () -> Void = {}
    var onMarkWatched: () -> Void = {}

    private var title: String {
        item.mediaModel.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? ""
    }
    private var imageURL: URL? {
        URL(string: item.mediaModel.coverImage ?? "")
    }
    private var canMarkWatched: Bool {
        item.haveNextEpisode
    }
    private var currentProgress: Int32 {
        Int32(truncating: item.mediaListModel.progress ?? 0)
    }
    private var nextProgress: Int32 {
        currentProgress + 1
    }

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            AsyncImage(url: imageURL) { img in
                img.resizable().scaledToFill()
            } placeholder: {
                Color.secondary.opacity(0.2)
            }
            .frame(width: 85)
            .frame(maxHeight: .infinity)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))

            VStack(alignment: .leading, spacing: 10) {
                Text(title)
                    .font(.headline)
                    .foregroundStyle(.secondary)

                CenterTextView(item: item, nextProgress: nextProgress)
                    .padding(.vertical, 8)

                Text(item.mediaModel.infoString())
                    .font(.caption2)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .onTapGesture(perform: onClick)
        .swipeActions(edge: .leading, allowsFullSwipe: true) {
            if canMarkWatched {
                Button(action: onMarkWatched) {
                    Label("Mark watched", systemImage: "bookmark")
                }
                .tint(.indigo)
            }
        }
        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
            Button(action: onDelete) {
                Label("Delete", systemImage: "trash")
            }
            .tint(.red)
        }
    }
}

private struct CenterTextView: View {
    let item: MediaWithMediaListItem
    let nextProgress: Int32

    var body: some View {
        if item.haveNextEpisode {
            Text("Next up: Episode \(nextProgress)")
                .font(.body)
                .foregroundColor(.primary)
                .fontWeight(.medium)
        } else if item.hasReleaseInfo {
            let nextEpisode = item.mediaModel.nextAiringEpisode?.episode ?? 0
            let durationUntilAir = item.mediaModel.releasingTimeString() ?? ""
            Text("Episode \(nextEpisode) in \(durationUntilAir)")
                .font(.body)
                .foregroundColor(.primary)
                .fontWeight(.medium)
        } else {
            Text("No upcoming episode")
                .font(.body)
                .foregroundColor(.primary)
                .fontWeight(.medium)
        }
    }
}
