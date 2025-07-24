package me.andannn.aniflow.service

import io.ktor.client.engine.HttpClientEngine
import me.andannn.network.engine.MockHttpClientEngine

internal actual val HttpEngine: HttpClientEngine = MockHttpClientEngine
