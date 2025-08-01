package me.andannn.aniflow.service

import io.ktor.client.engine.okhttp.OkHttp

actual val PlatformHttpClientEngine: io.ktor.client.engine.HttpClientEngine =
    OkHttp.create()
