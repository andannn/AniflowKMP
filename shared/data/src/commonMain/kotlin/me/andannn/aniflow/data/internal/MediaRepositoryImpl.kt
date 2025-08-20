/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.define.ContentMode
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.relation.MediaListAndMediaRelation
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.ServerException
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.Page
import kotlin.with

private const val TAG = "MediaRepository"

class MediaRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
    private val mediaService: AniListService,
    private val userPreference: UserSettingPreferences,
) : MediaRepository {
    override fun syncMediaCategory(
        scope: CoroutineScope,
        category: MediaCategory,
    ) = with(mediaService) {
        with(mediaLibraryDao) {
            category.syncLocalWithService(scope)
        }
    }

    override fun getMediasFlow(category: MediaCategory) =
        with(mediaLibraryDao) {
            getMediaOfCategoryFlow(Json.encodeToString(category))
                .map { list ->
                    CategoryWithContents(
                        category,
                        list.map(MediaEntity::toDomain),
                    )
                }
        }

    override fun getContentModeFlow(): Flow<ContentMode> =
        userPreference.userData
            .map { it.contentMode }
            .map { modeString ->
                if (modeString == null) {
                    Napier.w(tag = TAG) { "Content mode is null, using default value: ContentMode.LIST" }
                    ContentMode.ANIME
                } else {
                    Json.decodeFromString(modeString)
                }
            }.distinctUntilChanged()

    override suspend fun setContentMode(mode: ContentMode) {
        userPreference.setContentMode(Json.encodeToString(mode))
    }

    override fun syncMediaListByUserId(
        scope: CoroutineScope,
        userId: String,
        status: List<MediaListStatus>,
        mediaType: MediaType,
    ) = with(mediaService) {
        with(mediaLibraryDao) {
            // sync data from service
            syncMediaListInfoToLocal(
                userId = userId,
                status = status,
                mediaType = mediaType,
                scope = scope,
            )
        }
    }

    override fun getMediaListFlowByUserId(
        userId: String,
        mediaType: MediaType,
        mediaListStatus: List<MediaListStatus>,
    ): Flow<List<MediaWithMediaListItem>> =
        with(mediaService) {
            with(mediaLibraryDao) {
                getMediaListFlow(
                    userId = userId,
                    mediaType = Json.encodeToString(mediaType),
                    listStatus = mediaListStatus.map { Json.encodeToString(it) },
                ).map {
                    it.map(MediaListAndMediaRelation::toDomain)
                }
            }
        }

    override suspend fun loadMediaPageByCategory(
        category: MediaCategory,
        page: Int,
        perPage: Int,
    ) = with(mediaService) {
        category
            .getMediaOfCategoryFromRemote(
                page = page,
                perPage = perPage,
                displayAdultContent = false,
            ).toDomain(Media::toDomain)
    }
}

private const val DEFAULT_CACHED_SIZE = 20

context(service: AniListService, database: MediaLibraryDao)
private fun syncMediaListInfoToLocal(
    userId: String,
    status: List<MediaListStatus>,
    mediaType: MediaType,
    scope: CoroutineScope,
): Deferred<Throwable?> =
    scope.async {
        Napier.d(tag = TAG) { "syncMediaListInfoToLocal start: userId=$userId, status=$status, mediaType=$mediaType" }
        try {
            val mediaList =
                fetchAllMediaList(
                    userId = userId,
                    status = status,
                    mediaType = mediaType,
                )
            database.upsertMediaListEntities(mediaList.map(MediaList::toRelation))
            Napier.d(tag = TAG) { "syncMediaListInfoToLocal finished" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService)
suspend fun fetchAllMediaList(
    userId: String,
    status: List<MediaListStatus>,
    mediaType: MediaType,
): List<MediaList> {
    val acc = mutableListOf<MediaList>()
    var page = 1

    do {
        val res =
            service.getMediaListPage(
                userId = userId.toInt(),
                page = page++,
                perPage = 50,
                statusIn = status.map(MediaListStatus::toServiceType),
                type = mediaType.toServiceType(),
                format = me.andannn.aniflow.service.dto.enums.ScoreFormat.POINT_10_DECIMAL,
            )
        acc += res.items
    } while (res.pageInfo?.hasNextPage == true)

    return acc
}

context(service: AniListService, database: MediaLibraryDao)
private fun MediaCategory.syncLocalWithService(scope: CoroutineScope): Deferred<Throwable?> =
    scope.async {
        val category = this@syncLocalWithService
        Napier.d(tag = TAG) { "syncLocalWithService start: $category" }
        try {
            val items =
                getMediaOfCategoryFromRemote(
                    page = 1,
                    perPage = DEFAULT_CACHED_SIZE,
                    displayAdultContent = false,
                ).items
            database.upsertMediasWithCategory(
                category = Json.encodeToString(category),
                mediaList = items.map(Media::toEntity),
            )
            Napier.d(tag = TAG) { "syncLocalWithService finished: $category. size ${items.size}" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService)
private suspend fun MediaCategory.getMediaOfCategoryFromRemote(
    page: Int,
    perPage: Int,
    displayAdultContent: Boolean,
): Page<Media> {
    var status: MediaStatus?
    var seasonParam: AnimeSeasonParam?
    val type = this.mediaType()
    var sorts: List<MediaSort>? = null
    var format: List<MediaFormat>? = null
    var code: String? = null

    val currentSeasonParam = currentSeasonByLocalDataTime()

    when (this) {
        MediaCategory.CURRENT_SEASON_ANIME -> {
            status = null
            seasonParam = currentSeasonParam
            format =
                listOf(
                    MediaFormat.TV,
                    MediaFormat.TV_SHORT,
                    MediaFormat.OVA,
                    MediaFormat.ONA,
                    MediaFormat.ONE_SHOT,
                )
        }

        MediaCategory.NEXT_SEASON_ANIME -> {
            status = null
            seasonParam = currentSeasonParam.nextSeasonParam()
            format =
                listOf(
                    MediaFormat.TV,
                    MediaFormat.TV_SHORT,
                    MediaFormat.OVA,
                    MediaFormat.ONA,
                    MediaFormat.ONE_SHOT,
                )
        }

        MediaCategory.TRENDING_ANIME -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.MOVIE_ANIME -> {
            status = null
            seasonParam = null
            format = listOf(MediaFormat.MOVIE)
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.TRENDING_MANGA -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.ALL_TIME_POPULAR_MANGA -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.POPULARITY_DESC)
        }

        MediaCategory.TOP_MANHWA -> {
            status = null
            seasonParam = null
            code = "KR"
            sorts = listOf(MediaSort.POPULARITY_DESC)
        }

        MediaCategory.NEW_ADDED_ANIME -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.START_DATE_DESC)
        }

        MediaCategory.NEW_ADDED_MANGA -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.START_DATE_DESC)
        }
    }

    return service
        .getMediaPage(
            page = page,
            perPage = perPage,
            seasonYear = seasonParam?.seasonYear,
            season = seasonParam?.season?.toServiceType(),
            status = status,
            countryCode = code,
            isAdult = displayAdultContent,
            type = type.toServiceType(),
            formatIn = format?.map { it.toServiceType() },
            sort = sorts?.map { it.toServiceType() },
        )
}
