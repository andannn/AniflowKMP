package me.andannn.network.engine

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

val MockHttpClientEngine = MockEngine.create {
    addHandler { request ->
        respond(
            content = "",
            status = HttpStatusCode.OK,
            headers = headersOf("Content-Type" to listOf("application/json"))
        )
    }
}
