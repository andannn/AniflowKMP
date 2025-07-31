package me.andannn.aniflow.database

import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): app.cash.sqldelight.db.SqlDriver = NativeSqliteDriver(AniflowDatabase.Schema, DATABASE_NAME)
}
