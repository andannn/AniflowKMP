/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.DataWithErrors
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.ServerException
import me.andannn.aniflow.service.dto.Media

class MediaRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
    private val mediaService: AniListService,
) : MediaRepository {
    override fun getAllMediasWithCategory(mediaType: MediaType): Flow<DataWithErrors<Map<MediaCategory, List<MediaModel>>>> =
        channelFlow {
            with(mediaService) {
                with(mediaLibraryDao) {
                    val categories = mediaType.allCategories()

                    var dataMap = mapOf<MediaCategory, List<MediaModel>>()
                    val errorList = mutableListOf<Throwable>()
                    // sync data from service
                    launch {
                        supervisorScope {
                            val deferredList =
                                categories.map {
                                    it.syncLocalWithService()
                                }

                            deferredList
                                .awaitAll()
                                .filterNotNull()
                                .takeIf { it.isNotEmpty() }
                                ?.let { errors ->
                                    errorList.addAll(errors)
                                    send(DataWithErrors(dataMap, errors))
                                }
                        }
                    }

                    // emit data from database
                    val categoryToItemsFlowList =
                        categories.map { category ->
                            getMediaOfCategoryFlow(Json.encodeToString(category)).map {
                                category to it.map(MediaEntity::toDomain)
                            }
                        }

                    combine(
                        categoryToItemsFlowList,
                    ) { pairs ->
                        pairs.toMap()
                    }.collect {
                        dataMap = it
                        send(DataWithErrors(dataMap, errorList))
                    }
                }
            }
        }
}

private const val DEFAULT_CACHED_SIZE = 20

context(service: AniListService, database: MediaLibraryDao, scope: CoroutineScope)
private fun MediaCategory.syncLocalWithService(): Deferred<Throwable?> =
    scope.async {
        try {
            val items = getMediaOfCategoryFromRemote(MediaSeason.SUMMER, 2025)
            database.upsertMediasWithCategory(
                category = Json.encodeToString(this@syncLocalWithService),
                mediaList = items.map(Media::toEntity),
            )
            null
        } catch (exception: ServerException) {
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
