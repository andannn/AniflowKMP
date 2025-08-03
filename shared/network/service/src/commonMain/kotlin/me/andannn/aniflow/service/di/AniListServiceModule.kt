/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.di

import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.token.DataStoreTokenProvider
import org.koin.dsl.module

val serviceModule =
    module {
        single { AniListService(tokenProvider = DataStoreTokenProvider()) }
    }
