/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.di

import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.internal.AuthRepositoryImpl
import me.andannn.aniflow.data.internal.MediaRepositoryImpl
import me.andannn.aniflow.database.di.databaseModule
import me.andannn.aniflow.datastore.di.userPreferencesModule
import me.andannn.aniflow.service.di.serviceModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule =
    module {
        singleOf(::MediaRepositoryImpl).bind(MediaRepository::class)
        singleOf(::AuthRepositoryImpl).bind(AuthRepository::class)
        singleOf(::DataProvider)
        includes(
            databaseModule,
            serviceModule,
            userPreferencesModule,
        )
    }
