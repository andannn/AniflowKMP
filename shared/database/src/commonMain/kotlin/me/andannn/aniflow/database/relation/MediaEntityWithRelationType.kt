/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.database.relation

import me.andannn.aniflow.database.schema.MediaEntity

data class MediaEntityWithRelationType(
    val relationType: String,
    val media: MediaEntity,
) {
    companion object Companion {
        fun mapTo(
            relationType: String,
            id: String,
            mediaType: String?,
            englishTitle: String?,
            romajiTitle: String?,
            nativeTitle: String?,
            coverImageExtraLarge: String?,
            coverImageLarge: String?,
            coverImageMedium: String?,
            coverImageColor: String?,
            description: String?,
            episodes: Long?,
            seasonYear: Long?,
            season: String?,
            source: String?,
            status: String?,
            hashtag: String?,
            bannerImage: String?,
            averageScore: Long?,
            trailerId: String?,
            trailerSite: String?,
            trailerThumbnail: String?,
            genres: String?,
            format: String?,
            trending: Long?,
            favourites: Long?,
            popularRanking: Long?,
            ratedRanking: Long?,
            nextAiringEpisode: Long?,
            timeUntilAiring: Long?,
            isFavourite: Boolean?,
            siteUrl: String?,
            externalLinkList: String?,
            currentYearRanking: Long?,
            meanScore: Long?,
            currentYearPopularRanking: Long?,
        ): MediaEntityWithRelationType =
            MediaEntityWithRelationType(
                relationType = relationType,
                media =
                    MediaEntity(
                        id = id,
                        mediaType = mediaType,
                        englishTitle = englishTitle,
                        romajiTitle = romajiTitle,
                        nativeTitle = nativeTitle,
                        coverImageExtraLarge = coverImageExtraLarge,
                        coverImageLarge = coverImageLarge,
                        coverImageMedium = coverImageMedium,
                        coverImageColor = coverImageColor,
                        description = description,
                        episodes = episodes,
                        seasonYear = seasonYear,
                        season = season,
                        source = source,
                        status = status,
                        hashtag = hashtag,
                        bannerImage = bannerImage,
                        averageScore = averageScore,
                        trailerId = trailerId,
                        trailerSite = trailerSite,
                        trailerThumbnail = trailerThumbnail,
                        genres = genres,
                        format = format,
                        trending = trending,
                        favourites = favourites,
                        popularRanking = popularRanking,
                        ratedRanking = ratedRanking,
                        nextAiringEpisode = nextAiringEpisode,
                        timeUntilAiring = timeUntilAiring,
                        isFavourite = isFavourite,
                        siteUrl = siteUrl,
                        externalLinkList = externalLinkList,
                        currentYearRanking = currentYearRanking,
                        meanScore = meanScore,
                        currentYearPopularRanking = currentYearPopularRanking,
                    ),
            )
    }
}
