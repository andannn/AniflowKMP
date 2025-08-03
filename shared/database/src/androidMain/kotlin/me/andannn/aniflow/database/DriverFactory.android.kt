/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.database

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.mp.KoinPlatform.getKoin

actual fun createDriver(): SqlDriver =
    AndroidSqliteDriver(
        AniflowDatabase.Schema.synchronous(),
        getKoin().get<Context>(),
        DATABASE_NAME,
    )
