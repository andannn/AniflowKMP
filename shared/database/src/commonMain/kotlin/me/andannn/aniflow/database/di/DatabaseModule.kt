/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.database.di

import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.createDatabase
import me.andannn.aniflow.database.createDriver
import org.koin.dsl.module

val databaseModule =
    module {
        single { createDatabase(::createDriver) }
        single { MediaLibraryDao(get()) }
    }
