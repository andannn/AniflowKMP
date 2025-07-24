package me.andannn.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

/**
 *
 */
class AniListService(
    engine: HttpClientEngine
) {
    val client = HttpClient(engine) {

    }
}