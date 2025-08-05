/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.datastore.UserSettingPreferences
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserSettingPreferencesTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val datastoreScope = testScope.backgroundScope

    private lateinit var preferences: UserSettingPreferences

    private val randomInt
        get() = (0..1000).random()
    private val dataStoreFileName = "test.preferences_pb"

    @BeforeTest
    fun setUp() {
        val dataStore =
            PreferenceDataStoreFactory.createWithPath(
                scope = datastoreScope,
            ) {
                "${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/${randomInt}_$dataStoreFileName".toPath()
            }
        preferences = UserSettingPreferences(dataStore)
    }

    @Test
    fun setAndClearAuthTokenTest() =
        testScope.runTest {
            preferences.setAuthToken("test", 100)
            preferences.userData.first().let { userSettingPref ->
                assertEquals("test", userSettingPref.authToken)
                assertEquals(100, userSettingPref.authExpiredTimeInSecond)
            }

            preferences.clearAuthToken()

            preferences.userData.first().let { userSettingPref ->
                assertEquals(null, userSettingPref.authToken)
                assertEquals(null, userSettingPref.authExpiredTimeInSecond)
            }
        }
}
