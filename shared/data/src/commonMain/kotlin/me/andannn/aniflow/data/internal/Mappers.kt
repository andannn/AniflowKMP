/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.model.EpisodeModel
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.Trailer
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaSource
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.UserEntity
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.User
import me.andannn.aniflow.service.dto.enums.MediaRankType

internal fun MediaType.allCategories(): List<MediaCategory> =
    when (this) {
        MediaType.ANIME ->
            listOf(
                MediaCategory.CURRENT_SEASON_ANIME,
                MediaCategory.NEXT_SEASON_ANIME,
                MediaCategory.TRENDING_ANIME,
                MediaCategory.MOVIE_ANIME,
                MediaCategory.NEW_ADDED_ANIME,
            )

        MediaType.MANGA ->
            listOf(
                MediaCategory.TRENDING_MANGA,
                MediaCategory.ALL_TIME_POPULAR_MANGA,
                MediaCategory.TOP_MANHWA,
                MediaCategory.NEW_ADDED_MANGA,
            )
    }

internal fun MediaCategory.mediaType(): MediaType =
    when (this) {
        MediaCategory.CURRENT_SEASON_ANIME,
        MediaCategory.NEXT_SEASON_ANIME,
        MediaCategory.TRENDING_ANIME,
        MediaCategory.MOVIE_ANIME,
        MediaCategory.NEW_ADDED_ANIME,
        -> MediaType.ANIME

        MediaCategory.TRENDING_MANGA,
        MediaCategory.ALL_TIME_POPULAR_MANGA,
        MediaCategory.TOP_MANHWA,
        MediaCategory.NEW_ADDED_MANGA,
        -> MediaType.MANGA
    }

internal fun MediaType.toServiceType() =
    when (this) {
        MediaType.ANIME -> me.andannn.aniflow.service.dto.enums.MediaType.ANIME
        MediaType.MANGA -> me.andannn.aniflow.service.dto.enums.MediaType.MANGA
    }

internal fun me.andannn.aniflow.service.dto.enums.MediaType.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.MediaType.ANIME -> MediaType.ANIME
        me.andannn.aniflow.service.dto.enums.MediaType.MANGA -> MediaType.MANGA
        me.andannn.aniflow.service.dto.enums.MediaType.UNKNOWN__ -> null
    }

internal fun me.andannn.aniflow.service.dto.enums.MediaSource.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.MediaSource.ORIGINAL -> MediaSource.ORIGINAL
        me.andannn.aniflow.service.dto.enums.MediaSource.MANGA -> MediaSource.MANGA
        me.andannn.aniflow.service.dto.enums.MediaSource.LIGHT_NOVEL -> MediaSource.LIGHT_NOVEL
        me.andannn.aniflow.service.dto.enums.MediaSource.VISUAL_NOVEL -> MediaSource.VISUAL_NOVEL
        me.andannn.aniflow.service.dto.enums.MediaSource.VIDEO_GAME -> MediaSource.VIDEO_GAME
        me.andannn.aniflow.service.dto.enums.MediaSource.OTHER -> MediaSource.OTHER
        me.andannn.aniflow.service.dto.enums.MediaSource.NOVEL -> MediaSource.NOVEL
        me.andannn.aniflow.service.dto.enums.MediaSource.DOUJINSHI -> MediaSource.DOUJINSHI
        me.andannn.aniflow.service.dto.enums.MediaSource.ANIME -> MediaSource.ANIME
        me.andannn.aniflow.service.dto.enums.MediaSource.WEB_NOVEL -> MediaSource.WEB_NOVEL
        me.andannn.aniflow.service.dto.enums.MediaSource.LIVE_ACTION -> MediaSource.LIVE_ACTION
        me.andannn.aniflow.service.dto.enums.MediaSource.GAME -> MediaSource.GAME
        me.andannn.aniflow.service.dto.enums.MediaSource.COMIC -> MediaSource.COMIC
        me.andannn.aniflow.service.dto.enums.MediaSource.MULTIMEDIA_PROJECT -> MediaSource.MULTIMEDIA_PROJECT
        me.andannn.aniflow.service.dto.enums.MediaSource.PICTURE_BOOK -> MediaSource.PICTURE_BOOK
        me.andannn.aniflow.service.dto.enums.MediaSource.UNKNOWN__ -> null
    }

internal fun MediaSeason.toServiceType() =
    when (this) {
        MediaSeason.WINTER -> me.andannn.aniflow.service.dto.enums.MediaSeason.WINTER
        MediaSeason.SPRING -> me.andannn.aniflow.service.dto.enums.MediaSeason.SPRING
        MediaSeason.SUMMER -> me.andannn.aniflow.service.dto.enums.MediaSeason.SUMMER
        MediaSeason.FALL -> me.andannn.aniflow.service.dto.enums.MediaSeason.FALL
    }

internal fun me.andannn.aniflow.service.dto.enums.MediaSeason.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.MediaSeason.WINTER -> MediaSeason.WINTER
        me.andannn.aniflow.service.dto.enums.MediaSeason.SPRING -> MediaSeason.SPRING
        me.andannn.aniflow.service.dto.enums.MediaSeason.SUMMER -> MediaSeason.SUMMER
        me.andannn.aniflow.service.dto.enums.MediaSeason.FALL -> MediaSeason.FALL
        me.andannn.aniflow.service.dto.enums.MediaSeason.UNKNOWN__ -> null
    }

internal fun MediaFormat.toServiceType() =
    when (this) {
        MediaFormat.TV -> me.andannn.aniflow.service.dto.enums.MediaFormat.TV
        MediaFormat.TV_SHORT -> me.andannn.aniflow.service.dto.enums.MediaFormat.TV_SHORT
        MediaFormat.MOVIE -> me.andannn.aniflow.service.dto.enums.MediaFormat.MOVIE
        MediaFormat.SPECIAL -> me.andannn.aniflow.service.dto.enums.MediaFormat.SPECIAL
        MediaFormat.OVA -> me.andannn.aniflow.service.dto.enums.MediaFormat.OVA
        MediaFormat.ONA -> me.andannn.aniflow.service.dto.enums.MediaFormat.ONA
        MediaFormat.MUSIC -> me.andannn.aniflow.service.dto.enums.MediaFormat.MUSIC
        MediaFormat.MANGA -> me.andannn.aniflow.service.dto.enums.MediaFormat.MANGA
        MediaFormat.NOVEL -> me.andannn.aniflow.service.dto.enums.MediaFormat.NOVEL
        MediaFormat.ONE_SHOT -> me.andannn.aniflow.service.dto.enums.MediaFormat.ONE_SHOT
    }

internal fun me.andannn.aniflow.service.dto.enums.MediaFormat.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.MediaFormat.TV -> MediaFormat.TV
        me.andannn.aniflow.service.dto.enums.MediaFormat.TV_SHORT -> MediaFormat.TV_SHORT
        me.andannn.aniflow.service.dto.enums.MediaFormat.MOVIE -> MediaFormat.MOVIE
        me.andannn.aniflow.service.dto.enums.MediaFormat.SPECIAL -> MediaFormat.SPECIAL
        me.andannn.aniflow.service.dto.enums.MediaFormat.OVA -> MediaFormat.OVA
        me.andannn.aniflow.service.dto.enums.MediaFormat.ONA -> MediaFormat.ONA
        me.andannn.aniflow.service.dto.enums.MediaFormat.MUSIC -> MediaFormat.MUSIC
        me.andannn.aniflow.service.dto.enums.MediaFormat.MANGA -> MediaFormat.MANGA
        me.andannn.aniflow.service.dto.enums.MediaFormat.NOVEL -> MediaFormat.NOVEL
        me.andannn.aniflow.service.dto.enums.MediaFormat.ONE_SHOT -> MediaFormat.ONE_SHOT
        me.andannn.aniflow.service.dto.enums.MediaFormat.UNKNOWN__ -> null
    }

internal fun MediaSort.toServiceType() =
    when (this) {
        MediaSort.START_DATE -> me.andannn.aniflow.service.dto.enums.MediaSort.START_DATE
        MediaSort.START_DATE_DESC -> me.andannn.aniflow.service.dto.enums.MediaSort.START_DATE_DESC
        MediaSort.POPULARITY_DESC -> me.andannn.aniflow.service.dto.enums.MediaSort.POPULARITY_DESC
        MediaSort.TRENDING_DESC -> me.andannn.aniflow.service.dto.enums.MediaSort.TRENDING_DESC
        MediaSort.FAVOURITES_DESC -> me.andannn.aniflow.service.dto.enums.MediaSort.FAVOURITES_DESC
    }

internal fun me.andannn.aniflow.service.dto.enums.MediaStatus.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.MediaStatus.FINISHED -> MediaStatus.FINISHED
        me.andannn.aniflow.service.dto.enums.MediaStatus.RELEASING -> MediaStatus.RELEASING
        me.andannn.aniflow.service.dto.enums.MediaStatus.NOT_YET_RELEASED -> MediaStatus.NOT_YET_RELEASED
        me.andannn.aniflow.service.dto.enums.MediaStatus.CANCELLED -> MediaStatus.CANCELLED
        me.andannn.aniflow.service.dto.enums.MediaStatus.HIATUS -> MediaStatus.HIATUS
        me.andannn.aniflow.service.dto.enums.MediaStatus.UNKNOWN__ -> null
    }

internal fun Media.toEntity() =
    MediaEntity(
        id = id.toString(),
        mediaType = type?.toDomainType()?.let { Json.encodeToString(it) },
        englishTitle = title?.english,
        romajiTitle = title?.romaji,
        nativeTitle = title?.native,
        coverImageExtraLarge = coverImage?.extraLarge,
        coverImageLarge = coverImage?.large,
        coverImageMedium = coverImage?.medium,
        coverImageColor = coverImage?.color,
        hashtag = hashtag,
        description = description,
        source = source?.toDomainType()?.let { Json.encodeToString(it) },
        bannerImage = bannerImage,
        averageScore = averageScore?.toLong(),
        trending = trending?.toLong(),
        favourites = favourites?.toLong(),
        trailerId = trailer?.id,
        trailerSite = trailer?.site,
        trailerThumbnail = trailer?.thumbnail,
        episodes = episodes?.toLong(),
        season = season?.toDomainType()?.let { Json.encodeToString(it) },
        seasonYear = seasonYear?.toLong(),
        isFavourite = isFavourite,
        status = status?.toDomainType()?.let { Json.encodeToString(it) },
        format = format?.toDomainType()?.let { Json.encodeToString(it) },
        timeUntilAiring = nextAiringEpisode?.timeUntilAiring?.toLong(),
        nextAiringEpisode = nextAiringEpisode?.episode?.toLong(),
        genres = Json.encodeToString(genres),
        siteUrl = null,
        popularRanking = rankings?.firstOrNull { it?.type == MediaRankType.POPULAR }?.rank?.toLong(),
        ratedRanking = rankings?.firstOrNull { it?.type == MediaRankType.RATED }?.rank?.toLong(),
    )

internal fun MediaEntity.toDomain() =
    MediaModel(
        id = id,
        type = mediaType?.let { Json.decodeFromString(it) },
        title =
            Title(
                english = englishTitle,
                romaji = romajiTitle,
                native = nativeTitle,
            ),
        coverImage = coverImageLarge ?: coverImageMedium ?: coverImageExtraLarge,
        description = description,
        source = source?.let { Json.decodeFromString(it) },
        status = status?.let { Json.decodeFromString(it) },
        format = format?.let { Json.decodeFromString(it) },
        bannerImage = bannerImage,
        averageScore = averageScore?.toInt(),
        favourites = favourites?.toInt(),
        season = season?.let { Json.decodeFromString(it) },
        seasonYear = seasonYear?.toInt(),
        episodes = episodes?.toInt(),
        ratedRank = ratedRanking?.toInt(),
        popularRank = popularRanking?.toInt(),
        nextAiringEpisode =
            EpisodeModel(
                episode = nextAiringEpisode?.toInt(),
                timeUntilAiring = timeUntilAiring?.toInt(),
            ),
        isFavourite = isFavourite ?: false,
        hashtag = hashtag?.split(' ') ?: emptyList(),
        trailer =
            Trailer(
                site = trailerSite,
                thumbnail = trailerThumbnail,
            ),
    )

internal fun User.toEntity() =
    UserEntity(
        id = id.toString(),
        name = name,
        avatarImage = avatar?.large ?: avatar?.medium,
        bannerImage = bannerImage,
        profileColor = options?.profileColor,
        unreadNotificationCount = unreadNotificationCount?.toLong(),
    )

internal fun UserEntity.toDomain(): UserModel =
    UserModel(
        id = id,
        name = name,
        avatar = avatarImage,
        bannerImage = bannerImage,
        unreadNotificationCount = unreadNotificationCount?.toInt() ?: 0,
    )
