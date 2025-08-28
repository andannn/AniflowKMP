/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.model.ActivityNotification
import me.andannn.aniflow.data.model.EpisodeModel
import me.andannn.aniflow.data.model.FollowNotification
import me.andannn.aniflow.data.model.MediaDeletion
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.MediaNotification
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.PageInfo
import me.andannn.aniflow.data.model.SimpleDate
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.Trailer
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaSource
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.database.relation.MediaListAndMediaRelation
import me.andannn.aniflow.database.relation.MediaListAndMediaRelationWithUpdateLog
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.MediaListEntity
import me.andannn.aniflow.database.schema.UserEntity
import me.andannn.aniflow.service.dto.ActivityLikeNotification
import me.andannn.aniflow.service.dto.ActivityMentionNotification
import me.andannn.aniflow.service.dto.ActivityMessageNotification
import me.andannn.aniflow.service.dto.ActivityReplyLikeNotification
import me.andannn.aniflow.service.dto.ActivityReplyNotification
import me.andannn.aniflow.service.dto.ActivityReplySubscribedNotification
import me.andannn.aniflow.service.dto.AiringNotification
import me.andannn.aniflow.service.dto.FollowingNotification
import me.andannn.aniflow.service.dto.FuzzyDate
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaDataChangeNotification
import me.andannn.aniflow.service.dto.MediaDeletionNotification
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.MediaMergeNotification
import me.andannn.aniflow.service.dto.NotificationUnion
import me.andannn.aniflow.service.dto.Page
import me.andannn.aniflow.service.dto.RelatedMediaAdditionNotification
import me.andannn.aniflow.service.dto.User
import me.andannn.aniflow.service.dto.enums.MediaRankType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

internal fun me.andannn.aniflow.service.dto.enums.MediaListStatus.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.MediaListStatus.CURRENT -> MediaListStatus.CURRENT
        me.andannn.aniflow.service.dto.enums.MediaListStatus.PLANNING -> MediaListStatus.PLANNING
        me.andannn.aniflow.service.dto.enums.MediaListStatus.COMPLETED -> MediaListStatus.COMPLETED
        me.andannn.aniflow.service.dto.enums.MediaListStatus.DROPPED -> MediaListStatus.DROPPED
        me.andannn.aniflow.service.dto.enums.MediaListStatus.PAUSED -> MediaListStatus.PAUSED
        me.andannn.aniflow.service.dto.enums.MediaListStatus.REPEATING -> MediaListStatus.REPEATING
        me.andannn.aniflow.service.dto.enums.MediaListStatus.UNKNOWN__ -> null
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

internal fun MediaListStatus.toServiceType() =
    when (this) {
        MediaListStatus.CURRENT -> me.andannn.aniflow.service.dto.enums.MediaListStatus.CURRENT
        MediaListStatus.PLANNING -> me.andannn.aniflow.service.dto.enums.MediaListStatus.PLANNING
        MediaListStatus.COMPLETED -> me.andannn.aniflow.service.dto.enums.MediaListStatus.COMPLETED
        MediaListStatus.DROPPED -> me.andannn.aniflow.service.dto.enums.MediaListStatus.DROPPED
        MediaListStatus.PAUSED -> me.andannn.aniflow.service.dto.enums.MediaListStatus.PAUSED
        MediaListStatus.REPEATING -> me.andannn.aniflow.service.dto.enums.MediaListStatus.REPEATING
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

internal fun me.andannn.aniflow.service.dto.enums.UserTitleLanguage.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.ROMAJI -> UserTitleLanguage.ROMAJI
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.ENGLISH -> UserTitleLanguage.ENGLISH
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.NATIVE -> UserTitleLanguage.NATIVE
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.ROMAJI_STYLISED -> UserTitleLanguage.ROMAJI
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.ENGLISH_STYLISED -> UserTitleLanguage.ENGLISH
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.NATIVE_STYLISED -> UserTitleLanguage.NATIVE
        me.andannn.aniflow.service.dto.enums.UserTitleLanguage.UNKNOWN__ -> null
    }

internal fun me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage.toDomainType() =
    when (this) {
        me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage.NATIVE -> UserStaffNameLanguage.NATIVE
        me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage.ROMAJI -> UserStaffNameLanguage.ROMAJI
        me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage.ROMAJI_WESTERN -> UserStaffNameLanguage.ROMAJI_WESTERN
        me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage.UNKNOWN__ -> null
    }

internal fun Media.toDomain() = toEntity().toDomain()

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
        siteUrl = siteUrl,
    )

internal fun User.toEntity() =
    UserEntity(
        id = id.toString(),
        name = name,
        avatarImage = avatar?.large ?: avatar?.medium,
        bannerImage = bannerImage,
        profileColor = options?.profileColor,
        unreadNotificationCount = unreadNotificationCount?.toLong(),
        siteUrl = siteUrl,
    )

internal fun UserEntity.toDomain(): UserModel =
    UserModel(
        id = id,
        name = name,
        avatar = avatarImage,
        bannerImage = bannerImage,
        unreadNotificationCount = unreadNotificationCount?.toInt() ?: 0,
    )

internal fun MediaList.toRelation() =
    MediaListAndMediaRelation(
        mediaEntity =
            media?.toEntity()
                ?: error("Media cannot be null when converting to relation"),
        mediaListEntity = toEntity(media!!.id.toString()),
    )

internal fun MediaList.toEntity(mediaId: String) =
    MediaListEntity(
        mediaId = mediaId,
        mediaListId = id.toString(),
        userId = userId.toString(),
        listStatus = status?.toDomainType()?.let { Json.encodeToString(it) },
        progress = progress?.toLong(),
        notes = notes,
        repeat = repeat?.toLong(),
        isPrivate = private,
        updatedAt = updatedAt?.toLong(),
        score = score,
        progressVolumes = progressVolumes?.toLong(),
        startedAt = startedAt?.toSimpleDate()?.let { Json.encodeToString(it) },
        completedAt = completedAt?.toSimpleDate()?.let { Json.encodeToString(it) },
    )

internal fun MediaListEntity.toDomain() =
    MediaListModel(
        id = mediaListId,
        status = listStatus?.let { Json.decodeFromString<MediaListStatus>(it) },
        progress = progress?.toInt(),
        notes = notes,
        repeat = repeat?.toInt(),
        isPrivate = isPrivate ?: false,
        updatedAt = updatedAt?.toInt(),
        score = score,
        progressVolumes = progressVolumes?.toInt(),
        startedAt = startedAt?.let { Json.decodeFromString<SimpleDate>(it) },
        completedAt = completedAt?.let { Json.decodeFromString<SimpleDate>(it) },
    )

internal fun FuzzyDate.toSimpleDate(): SimpleDate? {
    return SimpleDate(
        year = year ?: return null,
        month = month ?: 1,
        day = day ?: 1,
    )
}

@OptIn(ExperimentalTime::class)
internal fun MediaListAndMediaRelationWithUpdateLog.toDomain() =
    MediaWithMediaListItem(
        mediaModel = mediaListAndMediaRelation.mediaEntity.toDomain(),
        mediaListModel = mediaListAndMediaRelation.mediaListEntity.toDomain(),
        airingScheduleUpdateTime = updateTime?.let { Instant.fromEpochSeconds(it) },
    )

internal fun <T, R> Page<T>.toDomain(mapper: (T) -> R) =
    me.andannn.aniflow.data.model.Page(
        pageInfo =
            PageInfo(
                total = pageInfo?.total ?: error("page info parameter is null"),
                perPage = pageInfo?.perPage ?: error("page info parameter is null"),
                currentPage = pageInfo?.currentPage ?: error("page info parameter is null"),
                lastPage = pageInfo?.lastPage ?: error("page info parameter is null"),
                hasNextPage = pageInfo?.hasNextPage ?: error("page info parameter is null"),
            ),
        items = items.map(mapper),
    )

internal fun NotificationUnion.toDomain(): NotificationModel =
    when (this) {
        is ActivityLikeNotification ->
            ActivityNotification.Like(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
                activityId = activityId,
            )

        is ActivityMentionNotification ->
            ActivityNotification.Mention(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
                activityId = activityId,
            )

        is ActivityMessageNotification ->
            ActivityNotification.Message(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
                activityId = activityId,
            )

        is ActivityReplyLikeNotification ->
            ActivityNotification.ReplyLike(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
                activityId = activityId,
            )

        is ActivityReplyNotification ->
            ActivityNotification.Reply(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
                activityId = activityId,
            )

        is ActivityReplySubscribedNotification ->
            ActivityNotification.ReplySubscribed(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
                activityId = activityId,
            )

        is AiringNotification ->
            me.andannn.aniflow.data.model.AiringNotification(
                id = id.toString(),
                context = contexts?.let { Json.encodeToString(it) } ?: "",
                createdAt = createdAt ?: 0,
                episode = episode,
                media = media!!.toDomain(),
            )

        is FollowingNotification ->
            FollowNotification(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                user = user!!.toEntity().toDomain(),
            )

        is MediaDataChangeNotification ->
            MediaNotification.MediaDataChange(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                media = media!!.toDomain(),
                reason = reason ?: "",
            )

        is MediaDeletionNotification ->
            MediaDeletion(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                deletedMediaTitle = deletedMediaTitle ?: "",
                reason = reason ?: "",
            )

        is MediaMergeNotification ->
            MediaNotification.MediaMerge(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                media = media!!.toDomain(),
            )

        is RelatedMediaAdditionNotification ->
            MediaNotification.RelatedMediaAddition(
                id = id.toString(),
                context = context ?: "",
                createdAt = createdAt ?: 0,
                media = media!!.toDomain(),
            )
    }
