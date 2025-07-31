package me.andannn.aniflow.database

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.mp.KoinPlatform.getKoin

actual class DriverFactory {
    actual fun createDriver(): app.cash.sqldelight.db.SqlDriver =
        AndroidSqliteDriver(AniflowDatabase.Schema, getKoin().get<Context>(), DATABASE_NAME)
}
