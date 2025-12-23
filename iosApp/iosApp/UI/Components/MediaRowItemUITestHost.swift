import SwiftUI
import Shared

/// Host view used exclusively by UI tests to exercise ``MediaRowSimple`` in predictable states.
struct MediaRowItemUITestHostView: View {
    private let items: [MediaWithMediaListItem] = [
        .uiTestNextEpisodeSample(),
        .uiTestUpcomingEpisodeSample(),
    ]

    var body: some View {
        List(items, id: \.mediaListModel.id) { item in
            MediaRowSimple(
                item: item,
                userTitleLanguage: .english
            )
            .padding(.vertical, 4)
            .accessibilityIdentifier("mediaRow-\(item.mediaListModel.id)")
        }
        .listStyle(.plain)
    }
}

private extension MediaWithMediaListItem {
    static func uiTestNextEpisodeSample() -> MediaWithMediaListItem {
        let title = Title(romaji: "Ao Example", english: "Blue Example", native: "ブルーサンプル")
        let nextEpisode = EpisodeModel(episode: 5, timeUntilAiring: 10800)
        let media = MediaModel(
            id: "next-episode",
            title: title,
            type: .anime,
            episodes: 24,
            seasonYear: 2024,
            season: .summer,
            source: .original,
            status: .releasing,
            isFavourite: false,
            coverImage: "https://example.com/cover-blue.png",
            format: .tv,
            nextAiringEpisode: nextEpisode
        )

        let mediaListModel = MediaListModel(
            id: "next-episode",
            status: .current,
            progress: 3
        )

        return MediaWithMediaListItem(
            mediaModel: media,
            mediaListModel: mediaListModel,
            airingScheduleUpdateTime: nil,
            firstAddedTime: nil
        )
    }

    static func uiTestUpcomingEpisodeSample() -> MediaWithMediaListItem {
        let title = Title(romaji: "Crimson Example", english: "Crimson Example", native: "紅のサンプル")
        let upcomingEpisode = EpisodeModel(episode: 6, timeUntilAiring: 7200)
        let media = MediaModel(
            id: "upcoming-episode",
            title: title,
            type: .anime,
            episodes: 6,
            seasonYear: 2023,
            season: .fall,
            source: .lightNovel,
            status: .hiatus,
            isFavourite: false,
            coverImage: "https://example.com/cover-red.png",
            format: .tv,
            nextAiringEpisode: upcomingEpisode
        )

        let mediaListModel = MediaListModel(
            id: "upcoming-episode",
            status: .paused,
            progress: 5
        )

        return MediaWithMediaListItem(
            mediaModel: media,
            mediaListModel: mediaListModel,
            airingScheduleUpdateTime: nil,
            firstAddedTime: nil
        )
    }
}
