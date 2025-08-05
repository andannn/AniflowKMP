/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.AuthToken
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.database.AniflowDatabase
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.TokenProvider
import me.andannn.network.engine.MockHttpClientEngine
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AuthRepositoryTest {
    private val testScope = TestScope()
    private val datastoreScope = testScope.backgroundScope
    lateinit var repo: AuthRepository
    private lateinit var driver: SqlDriver
    private lateinit var db: AniflowDatabase
    private lateinit var mediaLibraryDao: MediaLibraryDao
    private lateinit var userPreferences: UserSettingPreferences

    private val randomInt
        get() = (0..1000).random()
    private val dataStoreFileName = "test.preferences_pb"

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
        val preferences =
            PreferenceDataStoreFactory.createWithPath(
                scope = datastoreScope,
            ) {
                "${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/${randomInt}_$dataStoreFileName".toPath()
            }
        userPreferences = UserSettingPreferences(preferences)
        repo =
            AuthRepositoryImpl(
                database = mediaLibraryDao,
                service = service,
                authHandler =
                    object : BrowserAuthOperationHandler {
                        override fun openBrowser() {
                        }

                        override suspend fun awaitAuthResult(): AuthToken = AuthToken("DummyAccess", 100)
                    },
                userPref = userPreferences,
            )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun testGetAuthedUser() =
        testScope.runTest {
            repo
                .getAuthedUser()
                .first()
                .let {
                    println(it)
                }
        }
}
