package me.andannn.aniflow.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration

actual fun testDriver(): app.cash.sqldelight.db.SqlDriver {
    val schema = AniflowDatabase.Schema.synchronous()
    return NativeSqliteDriver(
        DatabaseConfiguration(
            name = DATABASE_NAME,
            version = schema.version.toInt(),
            create = { connection -> wrapConnection(connection) { schema.create(it) } },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
            },
            inMemory = true,
        ),
    )
}
