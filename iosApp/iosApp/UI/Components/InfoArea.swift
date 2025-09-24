import SwiftUI
import Shared

struct InfoArea: View {
    let mediaModel: MediaModel?

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let allTimeRatedRank = mediaModel?.allTimeRatedRank {
                InfoItemHorizon(
                    iconName: "star.fill",
                    isSystemImage: true,
                    contentText: "#\(allTimeRatedRank) Highest Rated All Time"
                )
            }

            if let currentYearRatedRank = mediaModel?.currentYearRatedRank,
               let mediaYear = mediaModel?.seasonYear {
                InfoItemHorizon(
                    iconName: "star.fill",
                    isSystemImage: true,
                    contentText: "#\(currentYearRatedRank) Highest Rated \(mediaYear)"
                )
            }

            if let allTimePopularRank = mediaModel?.allTimePopularRank {
                InfoItemHorizon(
                    iconName: "heart.fill",
                    isSystemImage: true,
                    contentText: "#\(allTimePopularRank) Most Popular All Time"
                )
            }

            // Only show "Most Popular $mediaYear" if allTimePopularRank is nil
            if let currentYearRatedRank = mediaModel?.currentYearRatedRank,
               let mediaYear = mediaModel?.seasonYear,
               mediaModel?.allTimePopularRank == nil {
                InfoItemHorizon(
                    iconName: "heart.fill",
                    isSystemImage: true,
                    contentText: "#\(currentYearRatedRank) Most Popular \(mediaYear)"
                )
            }

            if let averageScore = mediaModel?.averageScore {
                InfoItemHorizon(
                    iconName: "chart.bar.fill",
                    isSystemImage: true,
                    contentText: "Average Score \(averageScore)%"
                )
            }

            if let meanScore = mediaModel?.meanScore {
                InfoItemHorizon(
                    iconName: "chart.bar.fill",
                    isSystemImage: true,
                    contentText: "Mean Score \(meanScore)%"
                )
            }

            if let favourites = mediaModel?.favourites {
                InfoItemHorizon(
                    iconName: "hand.thumbsup.fill",
                    isSystemImage: true,
                    contentText: "Favourites \(favourites)"
                )
            }
        }
        .padding(.vertical, 12)
    }
}
