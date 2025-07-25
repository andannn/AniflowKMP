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
import me.andannn.aniflow.service.dto.AniListErrorResponse
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.dto.User
import me.andannn.aniflow.service.request.DetailMediaQuery
import me.andannn.aniflow.service.request.GetUserDataQuery
import me.andannn.aniflow.service.request.GraphQLQuery
import me.andannn.aniflow.service.request.toQueryBody

open class AniListServiceException(
    override val message: String,
) : IllegalStateException(message)

class UnauthorizedException(
    override val message: String,
) : AniListServiceException(message)

/**
 * Service for interacting with AniList GraphQL API.
 */
class AniListService(
    engine: HttpClientEngine = HttpEngine,
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
                        BearerTokens("def456", "xyz111")
                    }

                    refreshTokens {
                        BearerTokens("def456", "xyz111")
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
     *
     */
    suspend fun getAuthedUserData(): User =
        doGraphQlQuery(
            query = GetUserDataQuery,
        )

    /**
     *
     */
    suspend fun getDetailMedia(id: Int): MediaDetailResponse =
        doGraphQlQuery(
            query = DetailMediaQuery(id),
        )

    private suspend inline fun <reified T> doGraphQlQuery(query: GraphQLQuery<DataWrapper<T>>): T =
        try {
            client
                .post {
                    setBody(query.toQueryBody())
                }.let { response ->
                    val dataWrapper = response.body<DataWrapper<T>>()
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
