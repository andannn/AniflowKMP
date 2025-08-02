/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service

import io.ktor.client.engine.okhttp.OkHttp

actual val PlatformHttpClientEngine: io.ktor.client.engine.HttpClientEngine =
    OkHttp.create()
