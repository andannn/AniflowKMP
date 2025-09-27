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
        HStack(alignment: .top, spacing: 12) {
            CustomAsyncImage(url: item.mediaModel.coverImage)
                .frame(width: 85)
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))

            VStack(alignment: .leading, spacing: 8) {
                Text(title)
                    .font(.title3)
                    .fontWeight(.semibold)
                    .foregroundColor(.primary)
                    .lineLimit(2)
                    .truncationMode(.tail)
                    .padding(.top, 4)

                Spacer(minLength: 0)

                CenterTextView(item: item, nextProgress: nextProgress)
                    .padding(.vertical, 4)

                Spacer(minLength: 0)

                Text(item.mediaModel.infoString())
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
                    .padding(.bottom, 4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .contentShape(Rectangle())
        .onTapGesture(perform: onClick)
        .swipeActions(edge: .trailing, allowsFullSwipe: false) {
            if canMarkWatched {
                Button(action: onMarkWatched) {
                    Label {
                        Text("Mark watched")
                            .fontWeight(.bold)
                            .font(.body)
                    } icon: {
                        Image(systemName: "eye")
                    }
                }
                .tint(.indigo)
            }
        }
        .swipeActions(edge: .leading, allowsFullSwipe: false) {
            Button(action: onDelete) {
                Label {
                    Text("Delete")
                        .fontWeight(.bold)
                        .font(.body)
                } icon: {
                    Image(systemName: "trash.fill")
                }
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
                .fontWeight(.medium)
                .foregroundColor(.primary)
        } else if item.hasReleaseInfo {
            let nextEpisode = item.mediaModel.nextAiringEpisode?.episode ?? 0
            let durationUntilAir = item.mediaModel.releasingTimeString() ?? ""
            Text("Episode \(nextEpisode) in \(durationUntilAir)")
                .font(.body)
                .fontWeight(.medium)
                .foregroundColor(.primary)
        } else {
            Text("No upcoming episode")
                .font(.body)
                .fontWeight(.medium)
                .foregroundColor(.secondary)
        }
    }
}
