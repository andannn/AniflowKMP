package me.andannn.network.engine

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual val PlatformHttpClientEngine: HttpClientEngine = Darwin.create { }
