package me.andannn.aniflow.service

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TAG = "AniListService"

/**
 *
 */
class AniListService(
    engine: HttpClientEngine = HttpEngine
) {
    private val client = HttpClient(engine) {
        expectSuccess = true

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
            level = LogLevel.HEADERS
        }
    }
}

internal expect val HttpEngine: HttpClientEngine
