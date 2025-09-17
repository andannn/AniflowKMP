/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.database

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.database.relation.MediaListAndMediaRelation
import me.andannn.aniflow.database.util.MediaEntityWithDefault
import me.andannn.aniflow.database.util.MediaListEntityWithDefault
import me.andannn.aniflow.database.util.StudioEntityWithDefault
import me.andannn.aniflow.database.util.UserEntityWithDefault
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class MediaLibraryDaoTest {
    private lateinit var driver: SqlDriver
    private lateinit var db: AniflowDatabase
    private lateinit var mediaLibraryDao: MediaLibraryDao
    private val testScope = TestScope()

    @BeforeTest
    fun setup() {
        driver = testDriver()
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

    @Test
    fun testUpsertUser() =
        testScope.runTest {
            with(mediaLibraryDao) {
                upsertUser(
                    listOf(
                        UserEntityWithDefault(id = "user1", name = "User One"),
                        UserEntityWithDefault(id = "user2", name = "User Two"),
                    ),
                )

                getUserFlow("user1").firstOrNull()?.let {
                    assertEquals("user1", it.id)
                    assertEquals("User One", it.name)
                }
            }
        }

    @Test
    fun testGetMediaList() =
        testScope.runTest {
            with(mediaLibraryDao) {
                upsertMediaListEntities(
                    listOf(
                        MediaListAndMediaRelation(
                            mediaEntity =
                                MediaEntityWithDefault(
                                    id = "media1",
                                    englishTitle = "Media One",
                                    mediaType = "ANIME",
                                ),
                            mediaListEntity =
                                MediaListEntityWithDefault(
                                    mediaListId = "list1",
                                    userId = "user1",
                                    mediaId = "media1",
                                    listStatus = "CURRENT",
                                ),
                        ),
                    ),
                )

                getMediaListFlow(
                    userId = "user1",
                    mediaType = "ANIME",
                    listStatus = listOf("CURRENT", "PLANNING"),
                ).first().let { mediaList ->
                    assertEquals(1, mediaList.size)
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testGetNewReleasedMediaList() =
        testScope.runTest {
            with(mediaLibraryDao) {
                upsertMediaListEntities(
                    listOf(
                        MediaListAndMediaRelation(
                            mediaEntity =
                                MediaEntityWithDefault(
                                    id = "media1",
                                    englishTitle = "Media One",
                                    mediaType = "ANIME",
                                ),
                            mediaListEntity =
                                MediaListEntityWithDefault(
                                    mediaListId = "list1",
                                    userId = "user1",
                                    mediaId = "media1",
                                    listStatus = "CURRENT",
                                ),
                        ),
                    ),
                )

                db.airingUpdatedLogQueries.upsertAiringUpdatedLogForTest(
                    updatedMediaId = "media1",
                    updateTime =
                        Clock.System
                            .now()
                            .minus(12.hours)
                            .epochSeconds,
                )

                getNewReleasedMediaListFlow(
                    userId = "user1",
                    mediaType = "ANIME",
                    listStatus = listOf("CURRENT", "PLANNING"),
                    timeSecondLaterThan =
                        Clock.System
                            .now()
                            .minus(1.days)
                            .epochSeconds,
                ).first().let { mediaList ->
                    println(mediaList)
                    assertEquals(1, mediaList.size)
                }
            }
        }

    @Test
    fun testGetAndInsertRefreshTimestamp() =
        testScope.runTest {
            with(mediaLibraryDao) {
                val initialTimestamp = getRefreshTimeStamp("testCategory")
                assertNull(initialTimestamp)

                upsertRefreshTimeStamp("testCategory", 1234567890L)

                val updatedTimestamp = getRefreshTimeStamp("testCategory")
                assertEquals(1234567890L, updatedTimestamp)
            }
        }

    @Test
    fun getTrigger() =
        testScope.runTest {
            with(mediaLibraryDao) {
                upsertMediaListEntities(
                    listOf(
                        MediaListAndMediaRelation(
                            mediaEntity =
                                MediaEntityWithDefault(
                                    id = "media1",
                                    englishTitle = "Media One",
                                    mediaType = "ANIME",
                                    nextAiringEpisode = 1L,
                                ),
                            mediaListEntity =
                                MediaListEntityWithDefault(
                                    mediaListId = "list1",
                                    userId = "user1",
                                    mediaId = "media1",
                                    listStatus = "CURRENT",
                                ),
                        ),
                    ),
                )

                getMediaListFlow(
                    userId = "user1",
                    mediaType = "ANIME",
                    listStatus = listOf("CURRENT", "PLANNING"),
                ).first().let { mediaList ->
                    assertEquals(1, mediaList.size)
                    assertEquals(null, mediaList.first().updateTime)
                }

                upsertMedias(
                    listOf(
                        MediaEntityWithDefault(
                            id = "media1",
                            englishTitle = "Media One",
                            mediaType = "ANIME",
                            nextAiringEpisode = 2L,
                        ),
                    ),
                )

                getMediaListFlow(
                    userId = "user1",
                    mediaType = "ANIME",
                    listStatus = listOf("CURRENT", "PLANNING"),
                ).first().let { mediaList ->
                    assertNotNull(mediaList.first().updateTime)
                }
            }
        }

    @Test
    fun getStudioOfMediaTest() =
        testScope.runTest {
            with(mediaLibraryDao) {
                val studioList =
                    listOf(
                        StudioEntityWithDefault("studio1", "Studio A"),
                        StudioEntityWithDefault("studio2", "Studio B"),
                    )
                upsertStudiosOfMedia(
                    mediaId = "media1",
                    studios = studioList,
                )

                getStudiosOfMediaFlow("none").first().let {
                    assertEquals(0, it.size)
                }

                getStudiosOfMediaFlow("media1").first().let {
                    assertEquals(2, it.size)
                    assertEquals("studio1", it[0].id)
                    assertEquals("Studio A", it[0].name)
                    assertEquals("studio2", it[1].id)
                    assertEquals("Studio B", it[1].name)
                }
            }
        }
}
