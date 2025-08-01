package me.andannn.aniflow.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createDriver(): SqlDriver =
    NativeSqliteDriver(
        AniflowDatabase.Schema.synchronous(),
        DATABASE_NAME,
    )
