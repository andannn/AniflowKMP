/*
 * Copyright 2025, the AozoraBooks project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.datastore.di

import kotlinx.io.files.Path
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.datastore.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val fileDir: Path

private const val DATA_STORE_FILENAME = "user_setting.preferences_pb"

val userPreferencesModule: Module =
    module {
        single<UserSettingPreferences> {
            val datastore =
                createDataStore(
                    producePath = {
                        Path(fileDir, DATA_STORE_FILENAME).toString()
                    },
                )
            UserSettingPreferences(datastore)
        }
    }
