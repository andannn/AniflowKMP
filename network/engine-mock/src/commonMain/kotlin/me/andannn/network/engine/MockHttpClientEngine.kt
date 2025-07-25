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
import me.andannn.network.common.MediaDetailQuerySchema
import me.andannn.network.engine.mock.DetailAnimeData

val MockHttpClientEngine = MockEngine.create {
    addHandler { request ->
        request.body
            .takeIf { body ->
                body is TextContent
            }
            ?.let { body ->
                body as TextContent
                Json.decodeFromString<GraphQLBody>(body.text).let { query ->
                    when (query.query) {
                        MediaDetailQuerySchema -> {
                            println("MediaDetailQuerySchema")
                            respondMediaDetailQuery(query.variables)
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

private fun MockRequestHandleScope.respondMediaDetailQuery(
    variables: Map<String, String> = emptyMap(),
): HttpResponseData {
    return respond(
        content = DetailAnimeData,
        status = HttpStatusCode.OK,
        headers = headersOf("Content-Type" to listOf("application/json"))
    )
}