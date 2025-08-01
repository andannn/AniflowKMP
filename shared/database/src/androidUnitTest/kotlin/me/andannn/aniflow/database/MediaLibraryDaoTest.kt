package me.andannn.aniflow.database

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.database.util.MediaEntityWithDefault
import org.junit.Test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MediaLibraryDaoTest {
    private lateinit var driver: SqlDriver
    private lateinit var db: AniflowDatabase
    private lateinit var mediaLibraryDao: MediaLibraryDao
    private val testScope = TestScope()

    @BeforeTest
    fun setup() {
        driver = inMemoryDriver()
        db = AniflowDatabase(driver)
        mediaLibraryDao = MediaLibraryDao(db)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun testInsertAndQueryMedia() =
        testScope.runTest {
            with(mediaLibraryDao) {
                upsertMedias(
                    listOf(
                        MediaEntityWithDefault(id = "1"),
                        MediaEntityWithDefault(id = "2"),
                    ),
                )

                getMediaById("1").let {
                    assertNotNull(it)
                    assertEquals("1", it.id)
                }

                assertNull(getMediaById("3"))

                getMediasById(listOf("1", "2")).let {
                    assertEquals(2, it.size)
                    assertEquals("1", it[0].id)
                    assertEquals("2", it[1].id)
                }

                getMediaFlow("1").first().let {
                    assertNotNull(it)
                    assertEquals("1", it.id)
                }

                upsertMedias(
                    listOf(
                        MediaEntityWithDefault(id = "1"),
                    ),
                )

                assertEquals(
                    2,
                    db.mediaQueries
                        .getAllMedias()
                        .executeAsList()
                        .size,
                )
            }
        }

    @Test
    fun testUpsertMediasWithCategory() =
        testScope.runTest {
            with(mediaLibraryDao) {
                upsertMediasWithCategory(
                    category = "testCategory",
                    mediaList =
                        listOf(
                            MediaEntityWithDefault(id = "1"),
                            MediaEntityWithDefault(id = "2"),
                        ),
                )

                getMediaOfCategoryFlow("testCategory").first().let {
                    assertEquals(2, it.size)
                    assertEquals("1", it[0].id)
                    assertEquals("2", it[1].id)
                }
            }
        }
}
