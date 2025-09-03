import SwiftUI
import Shared

struct MediaRowSimple: View {
    let item: MediaWithMediaListItem
    let userTitleLanguage: UserTitleLanguage
    var onClick: () -> Void = {}
    var onDelete: () -> Void = {}
    var onMarkWatched: () -> Void = {}

    var body: some View {
        let title = item.mediaModel.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? ""
        let imageURL = URL(string: item.mediaModel.coverImage ?? "")
        let canMarkWatched: Bool = item.haveNextEpisode
        
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

                centerText
                    .padding(.vertical, 8)

                Text(item.mediaModel.infoString())
                    .font(.caption2)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .onTapGesture { onClick() }
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
        .swipeActions(edge: .trailing, allowsFullSwipe: true) {
            Button {
                onDelete()
            } label: {
                Label("Delete", systemImage: "trash")
            }
            .tint(.red)
        }
    }
    
    var centerText: some View {
        let currentProgress = item.mediaListModel.progress ?? 0
        let nextProgress = Int32(truncating: currentProgress) + 1

        if item.haveNextEpisode {
            return buildSpecialMessageText(
                "Next up: Episode \(nextProgress)",
                Color.primary
            )
        } else if item.hasReleaseInfo {
            let nextEpisode = item.mediaModel.nextAiringEpisode?.episode
            let durationUntilAir = item.mediaModel.releasingTimeString() ?? ""
            return buildSpecialMessageText(
                "Episode \(nextEpisode ?? 0) in \(durationUntilAir)",
                Color.primary
            )
        } else {
            return buildSpecialMessageText(
                "No upcoming episode",
                Color.primary
            )
        }
    }
}

func buildSpecialMessageText(_ text: String, _ numberColor: Color) -> Text {
    var result = Text("")
    for ch in text {
        if ch.isNumber {
            result = result + Text(String(ch))
                .font(.custom("EspecialMessageFontFamily", size: 30))
                .foregroundColor(numberColor)
        } else {
            result = result + Text(String(ch))
                .font(.custom("EspecialMessageFontFamily", size: 18))
        }
    }
    return result
}
