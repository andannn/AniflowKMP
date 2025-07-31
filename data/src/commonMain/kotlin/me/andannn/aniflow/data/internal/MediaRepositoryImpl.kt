package me.andannn.aniflow.data.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.service.AniListService

internal class MediaRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
    private val mediaService: AniListService,
) : MediaRepository {
    override fun getAllMediasWithCategory(mediaType: MediaType): Flow<Map<MediaCategory, List<MediaModel>>> =
        flow {
            val categories = mediaType.allCategories()

            categories.map { category -> }
        }
}

data class AnimeSeasonParam(
    val seasonYear: Int,
    val season: MediaSeason,
) {
    fun getNextSeasonParam(): AnimeSeasonParam =
        when (season) {
            MediaSeason.WINTER -> AnimeSeasonParam(seasonYear, MediaSeason.SPRING)
            MediaSeason.SPRING -> AnimeSeasonParam(seasonYear, MediaSeason.SUMMER)
            MediaSeason.SUMMER -> AnimeSeasonParam(seasonYear, MediaSeason.FALL)
            MediaSeason.FALL -> AnimeSeasonParam(seasonYear + 1, MediaSeason.WINTER)
        }
}

private suspend fun AniListService.getMediaOfCategory(
    category: MediaCategory,
    currentSeason: MediaSeason,
    currentSeasonYear: Int,
) {
    var status: MediaStatus? = null
    var seasonParam: AnimeSeasonParam? = null
    val type = category.mediaType()
    var sorts: List<MediaSort> = emptyList()
    var format: List<MediaFormat> = emptyList()
    var code: String? = null

    val currentSeasonParam =
        AnimeSeasonParam(
            seasonYear = currentSeasonYear,
            season = currentSeason,
        )

    when (category) {
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

//    getMediaPage(
//        page=  1,
//    perPage=  10,
//    seasonYear = seasonParam?.seasonYear,
//    season = seasonParam?.season,
//    status = status,
//        sort = sorts,
//    mediaFormat = format,
//    countryCode = code,
//    showAdultContents = null,
//
//        type= = mediaType,
//    )
}

private fun MediaType.allCategories(): List<MediaCategory> =
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

fun MediaCategory.mediaType(): MediaType =
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
