package me.andannn.aniflow.database

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "aniflow.db"

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): AniflowDatabase {
    val driver = driverFactory.createDriver()
    val database = AniflowDatabase(driver)

    return database
}
