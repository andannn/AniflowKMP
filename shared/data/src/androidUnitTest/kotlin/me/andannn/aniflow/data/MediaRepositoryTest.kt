package me.andannn.aniflow.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.data.internal.MediaRepositoryImpl
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.database.AniflowDatabase
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.TokenProvider
import me.andannn.network.engine.MockHttpClientEngine
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MediaRepositoryTest {
    private val testScope = TestScope()
    lateinit var repo: MediaRepository
    private lateinit var driver: SqlDriver
    private lateinit var db: AniflowDatabase
    private lateinit var mediaLibraryDao: MediaLibraryDao

    private val service: AniListService =
        AniListService(
            engine = MockHttpClientEngine,
            tokenProvider =
                object : TokenProvider {
                    override suspend fun getAccessToken(): String = "DummyAccessToken"
                },
        )

    @BeforeTest
    fun setup() {
        driver = inMemoryDriver()
        db = AniflowDatabase(driver)
        mediaLibraryDao = MediaLibraryDao(db)
        repo =
            MediaRepositoryImpl(
                mediaLibraryDao = mediaLibraryDao,
                mediaService = service,
            )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun testGetAllMediasWithCategory() =
        testScope.runTest {
            repo
                .getAllMediasWithCategory(
                    mediaType = MediaType.ANIME,
                ).first()
                .let {
                    println(it)
                }
        }
}

internal fun inMemoryDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        AniflowDatabase.Schema.synchronous().create(this)
    }
