package me.andannn.aniflow.service

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.request.DetailMediaQuery

private const val TAG = "AniListService"

/**
 *
 */
class AniListService(
    engine: HttpClientEngine = HttpEngine
) {
    private val client = HttpClient(engine) {
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
                },
            )
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

    suspend fun getDetailMedia(id: Int): MediaDetailResponse {
        return doGraphQlQuery(
            query = DetailMediaQuery(id)
        )
    }

    private suspend inline fun <reified T> doGraphQlQuery(
        query: GraphQLQuery<DataWrapper<T>>
    ): T {
        return client.post {
            setBody(query.toQueryBody())
        }.let { response ->
            val dataWrapper = response.body<DataWrapper<T>>()
            dataWrapper.data
        }
    }
}

internal expect val HttpEngine: HttpClientEngine
