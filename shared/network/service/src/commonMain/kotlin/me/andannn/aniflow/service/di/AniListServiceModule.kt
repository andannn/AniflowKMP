/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.di

import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.DummyTokenProvider
import me.andannn.aniflow.service.token.DataStoreTokenProvider
import me.andannn.network.engine.MockHttpClientEngine
import org.koin.dsl.module

val serviceModule =
    module {
//        single { AniListService(tokenProvider = DataStoreTokenProvider(get()), engine = PlatformHttpClientEngine) }
        single { AniListService(tokenProvider = DummyTokenProvider(), engine = MockHttpClientEngine) }
    }
