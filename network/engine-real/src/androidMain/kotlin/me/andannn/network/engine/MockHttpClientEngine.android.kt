package me.andannn.network.engine

import io.ktor.client.engine.okhttp.OkHttp


actual val PlatformHttpClientEngine: io.ktor.client.engine.HttpClientEngine = OkHttp.create()