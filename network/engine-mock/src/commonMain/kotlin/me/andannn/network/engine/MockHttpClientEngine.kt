package me.andannn.network.engine

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json
import me.andannn.network.common.GraphQLBody
import me.andannn.network.common.schemas.MEDIA_DETAIL_QUERY_SCHEMA
import me.andannn.network.common.schemas.MEDIA_PAGE_QUERY_SCHEMA
import me.andannn.network.common.schemas.USER_DATA_MUTATION_SCHEMA
import me.andannn.network.engine.mock.DETAIL_ANIME_DATA
import me.andannn.network.engine.mock.MEDIA_PAGE_DATA
import me.andannn.network.engine.mock.UNAUTHORIZED_ERROR
import me.andannn.network.engine.mock.USER_DATA

val MockHttpClientEngine =
    MockEngine.create {
        addHandler { request ->
            val hasToken = request.headers["Authorization"] == "Bearer DummyAccessToken"
            request.body
                .takeIf { body ->
                    body is TextContent
                }?.let { body ->
                    body as TextContent
                    Json.decodeFromString<GraphQLBody>(body.text).let { query ->
                        when (query.query) {
                            MEDIA_DETAIL_QUERY_SCHEMA -> {
                                println("MediaDetailQuerySchema")
                                respondString(DETAIL_ANIME_DATA)
                            }

                            USER_DATA_MUTATION_SCHEMA -> {
                                println("UserDataMutationSchema")
                                if (hasToken) {
                                    respondString(USER_DATA)
                                } else {
                                    respondUnAuthed()
                                }
                            }

                            MEDIA_PAGE_QUERY_SCHEMA -> {
                                println("MediaPageQuerySchema")
                                respondString(MEDIA_PAGE_DATA)
                            }

                            else -> error("Not supported query: $query")
                        }
                    }
                }
                ?: run {
                    error("Unsupported request body: ${request.body}")
                }
        }
    }

private fun MockRequestHandleScope.respondString(string: String): HttpResponseData =
    respond(
        content = string,
        status = HttpStatusCode.OK,
        headers = headersOf("Content-Type" to listOf("application/json")),
    )

private fun MockRequestHandleScope.respondUnAuthed(): HttpResponseData =
    respond(
        content = UNAUTHORIZED_ERROR,
        status = HttpStatusCode.Unauthorized,
        headers = headersOf("Content-Type" to listOf("application/json")),
    )
