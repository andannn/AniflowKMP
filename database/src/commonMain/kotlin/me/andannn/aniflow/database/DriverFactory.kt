package me.andannn.aniflow.database

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "aniflow.db"

expect fun createDriver(): SqlDriver

fun createDatabase(driverFactory: () -> SqlDriver): AniflowDatabase {
    val driver = driverFactory()
    val database = AniflowDatabase(driver)

    return database
}
