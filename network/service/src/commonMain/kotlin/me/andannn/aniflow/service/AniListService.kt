/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.andannn.aniflow.service.dto.ActivityUnion
import me.andannn.aniflow.service.dto.AiringSchedule
import me.andannn.aniflow.service.dto.AniListErrorResponse
import me.andannn.aniflow.service.dto.Character
import me.andannn.aniflow.service.dto.CharactersConnection
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.Page
import me.andannn.aniflow.service.dto.Staff
import me.andannn.aniflow.service.dto.StaffConnection
import me.andannn.aniflow.service.dto.Studio
import me.andannn.aniflow.service.dto.UpdateUserRespond
import me.andannn.aniflow.service.dto.enums.ActivityType
import me.andannn.aniflow.service.dto.enums.MediaFormat
import me.andannn.aniflow.service.dto.enums.MediaListStatus
import me.andannn.aniflow.service.dto.enums.MediaSeason
import me.andannn.aniflow.service.dto.enums.MediaSort
import me.andannn.aniflow.service.dto.enums.MediaStatus
import me.andannn.aniflow.service.dto.enums.MediaType
import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.aniflow.service.dto.enums.StaffLanguage
import me.andannn.aniflow.service.request.ActivityPageScheduleQuery
import me.andannn.aniflow.service.request.AiringScheduleQuery
import me.andannn.aniflow.service.request.CharacterDetailQuery
import me.andannn.aniflow.service.request.CharacterPageQuery
import me.andannn.aniflow.service.request.DetailMediaQuery
import me.andannn.aniflow.service.request.GetUserDataQuery
import me.andannn.aniflow.service.request.GraphQLQuery
import me.andannn.aniflow.service.request.MediaListPageQuery
import me.andannn.aniflow.service.request.MediaListQuery
import me.andannn.aniflow.service.request.MediaPageQuery
import me.andannn.aniflow.service.request.SearchCharacterQuery
import me.andannn.aniflow.service.request.SearchMediaQuery
import me.andannn.aniflow.service.request.SearchStaffQuery
import me.andannn.aniflow.service.request.SearchStudioQuery
import me.andannn.aniflow.service.request.StaffPageQuery
import me.andannn.aniflow.service.request.ToggleFavoriteMutation
import me.andannn.aniflow.service.request.toQueryBody

open class AniListServiceException(
    override val message: String,
) : IllegalStateException(message)

class UnauthorizedException(
    override val message: String,
) : AniListServiceException(message)

class TokenExpiredException(
    override val message: String,
) : AniListServiceException(message)

/**
 * Service for interacting with AniList GraphQL API.
 */
class AniListService(
    engine: HttpClientEngine = HttpEngine,
    tokenProvider: TokenProvider,
) {
    private val client =
        HttpClient(engine) {
            expectSuccess = true

            defaultRequest {
                url("https://graphql.anilist.co")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    },
                )
            }

            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }

            install(Auth) {
                bearer {
                    sendWithoutRequest {
                        // send token only if response is 401 Unauthorized
                        false
                    }

                    loadTokens {
                        tokenProvider.getAccessToken()?.let { accessToken ->
                            BearerTokens(
                                accessToken = accessToken,
                                refreshToken = null,
                            )
                        } ?: throw UnauthorizedException("No access token available")
                    }

                    refreshTokens {
                        throw TokenExpiredException("Refresh token is not supported")
                    }
                }
            }

            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            Napier.d(tag = TAG) { message }
                        }
                    }
                level = LogLevel.ALL
            }
        }

    /**
     * Fetches the authenticated user's data.
     *
     * @throws UnauthorizedException if no access token is available or the token is invalid.
     */
    suspend fun getAuthedUserData(): UpdateUserRespond = doGraphQlQuery(query = GetUserDataQuery)

    /**
     * Fetches detailed media information by its ID.
     */
    suspend fun getDetailMedia(id: Int): MediaDetailResponse = doGraphQlQuery(query = DetailMediaQuery(id))

    /**
     * Fetches a paginated list of media based on various filters and sorting options.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param type The type of media to filter by (optional).
     * @param countryCode The country code to filter by (optional).
     * @param seasonYear The year of the season to filter by (optional).
     * @param season The season to filter by (optional).
     * @param status The status of the media to filter by (optional).
     * @param sort A list of sorting options (optional).
     * @param formatIn A list of formats to filter by (optional).
     * @param isAdult Whether to include adult content (optional).
     * @param startDateGreater Start date greater than this value (optional).
     * @param endDateLesser End date lesser than this value (optional).
     */
    suspend fun getMediaPage(
        page: Int = 1,
        perPage: Int = 10,
        type: MediaType? = null,
        countryCode: String? = null,
        seasonYear: Int? = null,
        season: MediaSeason? = null,
        status: MediaStatus? = null,
        sort: List<MediaSort>? = null,
        formatIn: List<MediaFormat>? = null,
        isAdult: Boolean? = null,
        startDateGreater: String? = null,
        endDateLesser: String? = null,
    ): Page<Media> =
        doGraphQlQuery(
            query =
                MediaPageQuery(
                    page = page,
                    perPage = perPage,
                    type = type,
                    countryCode = countryCode,
                    seasonYear = seasonYear,
                    season = season,
                    status = status,
                    sort = sort,
                    formatIn = formatIn,
                    isAdult = isAdult,
                    startDateGreater = startDateGreater,
                    endDateLesser = endDateLesser,
                ),
        ).page

    /**
     * Fetches a paginated list of characters associated with a specific media.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param mediaId The ID of the media to fetch characters for.
     * @param staffLanguage The language of the staff to filter by.
     */
    suspend fun getCharacterPagesOfMedia(
        page: Int = 1,
        perPage: Int = 10,
        mediaId: Int,
        staffLanguage: StaffLanguage,
    ): CharactersConnection? =
        doGraphQlQuery(
            query =
                CharacterPageQuery(
                    page = page,
                    perPage = perPage,
                    mediaId = mediaId,
                    staffLanguage = staffLanguage,
                ),
        ).media.characters

    /**
     * Fetches a paginated list of staff associated with a specific media.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param mediaId The ID of the media to fetch staff for.
     */
    suspend fun getStaffPagesOfMedia(
        page: Int = 1,
        perPage: Int = 10,
        mediaId: Int,
    ): StaffConnection? =
        doGraphQlQuery(
            query =
                StaffPageQuery(
                    page = page,
                    perPage = perPage,
                    mediaId = mediaId,
                ),
        ).media.staff

    /**
     * Fetches a specific media list item by its media ID and user ID.
     *
     * @param mediaId The ID of the media to fetch.
     * @param userId The ID of the user whose media list item to fetch.
     * @param scoreFormat The format of the score to return.
     */
    suspend fun getMediaListItem(
        mediaId: Int,
        userId: Int,
        scoreFormat: ScoreFormat,
    ): MediaList? =
        doGraphQlQuery(
            query =
                MediaListQuery(
                    mediaId,
                    userId,
                    scoreFormat,
                ),
        ).mediaList

    /**
     * Fetches a paginated list of media list items for a specific user.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param userId The ID of the user whose media list to fetch.
     * @param statusIn A list of media list statuses to filter by.
     * @param type The type of media to filter by.
     * @param format The format of the score to return.
     */
    suspend fun getMediaListPage(
        page: Int = 1,
        perPage: Int = 10,
        userId: Int,
        statusIn: List<MediaListStatus>,
        type: MediaType,
        format: ScoreFormat,
    ): Page<MediaList> =
        doGraphQlQuery(
            query =
                MediaListPageQuery(
                    page = page,
                    perPage = perPage,
                    userId = userId,
                    statusIn = statusIn,
                    type = type,
                    format = format,
                ),
        ).page

    /**
     * Fetches a paginated list of airing schedules based on airing times.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param airingAtGreater The minimum airing time (in seconds since epoch) to filter by.
     * @param airingAtLesser The maximum airing time (in seconds since epoch) to filter by.
     */
    suspend fun getAiringSchedulePage(
        page: Int,
        perPage: Int,
        airingAtGreater: Int,
        airingAtLesser: Int,
    ): Page<AiringSchedule> =
        doGraphQlQuery(
            query =
                AiringScheduleQuery(
                    page = page,
                    perPage = perPage,
                    airingAtGreater = airingAtGreater,
                    airingAtLesser = airingAtLesser,
                ),
        ).page

    /**
     * Searches for media based on a keyword and various filters.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param keyword The keyword to search for in media titles.
     * @param type The type of media to filter by.
     * @param isAdult Whether to include adult content in the search results.
     */
    suspend fun searchMedia(
        page: Int,
        perPage: Int,
        keyword: String,
        type: MediaType,
        isAdult: Boolean,
    ): Page<Media> =
        doGraphQlQuery(
            query =
                SearchMediaQuery(
                    page = page,
                    perPage = perPage,
                    keyword = keyword,
                    type = type,
                    isAdult = isAdult,
                ),
        ).page

    /**
     * Searches for characters based on a keyword.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param keyword The keyword to search for in character names.
     */
    suspend fun searchCharacter(
        page: Int,
        perPage: Int,
        keyword: String,
    ): Page<Character> =
        doGraphQlQuery(
            query =
                SearchCharacterQuery(
                    page = page,
                    perPage = perPage,
                    keyword = keyword,
                ),
        ).page

    /**
     * Searches for studios based on a keyword.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param keyword The keyword to search for in studio names.
     */
    suspend fun searchStudio(
        page: Int,
        perPage: Int,
        keyword: String,
    ): Page<Studio> =
        doGraphQlQuery(
            query =
                SearchStudioQuery(
                    page = page,
                    perPage = perPage,
                    keyword = keyword,
                ),
        ).page

    /**
     * Searches for studios based on a keyword.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     * @param keyword The keyword to search for in studio names.
     */
    suspend fun searchStaff(
        page: Int,
        perPage: Int,
        keyword: String,
    ): Page<Staff> =
        doGraphQlQuery(
            query =
                SearchStaffQuery(
                    page = page,
                    perPage = perPage,
                    keyword = keyword,
                ),
        ).page

    /**
     * Fetches a paginated list of staff members based on various filters.
     *
     * @param page The page number to fetch (default is 1).
     * @param perPage The number of items per page (default is 10).
     */
    suspend fun getActivities(
        page: Int,
        perPage: Int,
        isFollowing: Boolean? = null,
        typeIn: List<ActivityType> = emptyList(),
        userId: Int? = null,
        mediaId: Int? = null,
        hasRepliesOrTypeText: Boolean? = null,
    ): Page<ActivityUnion> =
        doGraphQlQuery(
            query =
                ActivityPageScheduleQuery(
                    page = page,
                    perPage = perPage,
                    userId = userId,
                    typeIn = typeIn,
                    mediaId = mediaId,
                    isFollowing = isFollowing,
                    hasRepliesOrTypeText = hasRepliesOrTypeText,
                ),
        ).page

    /**
     * Toggles the favorite status of a media item, manga, character, staff, or studio.
     *
     * This method allows you to add or remove an item from the user's favorites.
     * @param mediaId The ID of the media item to toggle (optional).
     * @param mangaId The ID of the manga to toggle (optional).
     * @param characterId The ID of the character to toggle (optional).
     * @param staffId The ID of the staff member to toggle (optional).
     * @param studioId The ID of the studio to toggle (optional).
     */
    suspend fun toggleFavorite(
        mediaId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null,
    ): Unit =
        doGraphQlQuery(
            query =
                ToggleFavoriteMutation(
                    mediaId = mediaId,
                    mangaId = mangaId,
                    characterId = characterId,
                    staffId = staffId,
                    studioId = studioId,
                ),
        )

    suspend fun getCharacterDetail(id: Int): Character? =
        doGraphQlQuery(
            query =
                CharacterDetailQuery(
                    id = id,
                ),
        ).character

    private suspend inline fun <reified T : GraphQLQuery<DataWrapper<U>>, reified U> doGraphQlQuery(query: T): U =
        try {
            client
                .post {
                    setBody(query.toQueryBody())
                }.let { response ->
                    val dataWrapper = response.body<DataWrapper<U>>()
                    dataWrapper.data
                }
        } catch (exception: ResponseException) {
            throw exception.toAniListException()
        }
}

private suspend fun ResponseException.toAniListException(): AniListServiceException {
    val error = response.body<AniListErrorResponse>()
    return when (response.status) {
        HttpStatusCode.Unauthorized -> {
            UnauthorizedException(error.errors.first().message)
        }

        else -> {
            AniListServiceException(error.errors.first().message)
        }
    }
}

private const val TAG = "AniListService"

internal expect val HttpEngine: HttpClientEngine
