package me.andannn.network.engine

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import me.andannn.network.common.GraphQLBody
import me.andannn.network.common.schemas.MEDIA_DETAIL_QUERY_SCHEMA
import me.andannn.network.common.schemas.USER_DATA_MUTATION_SCHEMA
import me.andannn.network.engine.mock.DETAIL_ANIME_DATA
import me.andannn.network.engine.mock.UNAUTHORIZED_ERROR

val MockHttpClientEngine =
    MockEngine.create {
        addHandler { request ->
            request.body
                .takeIf { body ->
                    body is TextContent
                }?.let { body ->
                    body as TextContent
                    Json.decodeFromString<GraphQLBody>(body.text).let { query ->
                        when (query.query) {
                            MEDIA_DETAIL_QUERY_SCHEMA -> {
                                println("MediaDetailQuerySchema")
                                respondMediaDetailQuery(query.variables)
                            }

                            USER_DATA_MUTATION_SCHEMA -> {
                                println("UserDataMutationSchema")
                                respondUnAuthed()
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

private fun MockRequestHandleScope.respondUnAuthed(): HttpResponseData =
    respond(
        content = UNAUTHORIZED_ERROR,
        status = HttpStatusCode.Unauthorized,
        headers = headersOf("Content-Type" to listOf("application/json")),
    )

private fun MockRequestHandleScope.respondMediaDetailQuery(variables: Map<String, JsonPrimitive> = emptyMap()): HttpResponseData =
    respond(
        content = DETAIL_ANIME_DATA,
        status = HttpStatusCode.OK,
        headers = headersOf("Content-Type" to listOf("application/json")),
    )
