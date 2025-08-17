/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.DataWithErrorFlow
import me.andannn.aniflow.data.model.DataWithErrors
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.relation.MediaListAndMediaRelation
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.ServerException
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaList

private const val TAG = "MediaRepository"

class MediaRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
    private val mediaService: AniListService,
) : MediaRepository {
    override fun getMediasFlow(category: MediaCategory) =
        with(mediaService) {
            with(mediaLibraryDao) {
                val dataFlow =
                    mediaLibraryDao
                        .getMediaOfCategoryFlow(Json.encodeToString(category))
                        .map { list ->
                            CategoryWithContents(
                                category,
                                list.map(MediaEntity::toDomain),
                            )
                        }.distinctUntilChanged()

                val errorsFlow = MutableSharedFlow<Throwable>(replay = 1)
                val syncedDataFlow =
                    dataFlow.onStart {
                        coroutineScope {
                            val error =
                                category
                                    .syncLocalWithService(this)
                                    .await()
                            if (error != null) {
                                errorsFlow.emit(error)
                            }
                        }
                    }

                DataWithErrorFlow(
                    dataFlow = syncedDataFlow,
                    errorFlow = errorsFlow,
                )
            }
        }

    override fun getMediaListFlowByUserId(
        userId: String,
        mediaType: MediaType,
        mediaListStatus: List<MediaListStatus>,
    ): Flow<DataWithErrors<List<MediaWithMediaListItem>>> =
        channelFlow {
            with(mediaService) {
                with(mediaLibraryDao) {
                    var data: List<MediaWithMediaListItem> = emptyList()
                    val errorList = mutableListOf<Throwable>()

                    // sync data from service
                    launch {
                        syncMediaListInfoToLocal(
                            userId = userId,
                            status = mediaListStatus,
                            mediaType = mediaType,
                            scope = this@channelFlow,
                        ).await()?.let { error ->
                            errorList.add(error)
                            send(DataWithErrors(data, errorList))
                        }
                    }

                    getMediaListFlow(
                        userId = userId,
                        mediaType = Json.encodeToString(mediaType),
                        listStatus = mediaListStatus.map { Json.encodeToString(it) },
                    ).collect {
                        data = it.map(MediaListAndMediaRelation::toDomain)
                        send(DataWithErrors(data, errorList))
                    }
                }
            }
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
            val items = getMediaOfCategoryFromRemote(MediaSeason.SUMMER, 2025)
            database.upsertMediasWithCategory(
                category = Json.encodeToString(category),
                mediaList = items.map(Media::toEntity),
            )
            Napier.d(tag = TAG) { "syncLocalWithService finished: $category" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService)
private suspend fun MediaCategory.getMediaOfCategoryFromRemote(
    currentSeason: MediaSeason,
    currentSeasonYear: Int,
): List<Media> {
    var status: MediaStatus?
    var seasonParam: AnimeSeasonParam?
    val type = this.mediaType()
    var sorts: List<MediaSort> = emptyList()
    var format: List<MediaFormat> = emptyList()
    var code: String? = null

    val currentSeasonParam =
        AnimeSeasonParam(
            seasonYear = currentSeasonYear,
            season = currentSeason,
        )

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
            seasonParam = currentSeasonParam.getNextSeasonParam()
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
            format = emptyList()
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.ALL_TIME_POPULAR_MANGA -> {
            status = null
            seasonParam = null
            format = emptyList()
            sorts = listOf(MediaSort.POPULARITY_DESC)
        }

        MediaCategory.TOP_MANHWA -> {
            status = null
            seasonParam = null
            code = "KR"
            format = emptyList()
            sorts = listOf(MediaSort.POPULARITY_DESC)
        }

        MediaCategory.NEW_ADDED_ANIME -> {
            status = null
            seasonParam = null
            format = emptyList()
            sorts = listOf(MediaSort.START_DATE_DESC)
        }

        MediaCategory.NEW_ADDED_MANGA -> {
            status = null
            seasonParam = null
            format = emptyList()
            sorts = listOf(MediaSort.START_DATE_DESC)
        }
    }

    return service
        .getMediaPage(
            page = 1,
            perPage = DEFAULT_CACHED_SIZE,
            seasonYear = seasonParam?.seasonYear,
            season = seasonParam?.season?.toServiceType(),
            status = status,
            countryCode = code,
            isAdult = false,
            type = type.toServiceType(),
            formatIn = format.map { it.toServiceType() },
            sort = sorts.map { it.toServiceType() },
        ).items
}
