package me.andannn.aniflow.service

import io.ktor.client.engine.darwin.Darwin

actual val PlatformHttpClientEngine: io.ktor.client.engine.HttpClientEngine = Darwin.create { }
