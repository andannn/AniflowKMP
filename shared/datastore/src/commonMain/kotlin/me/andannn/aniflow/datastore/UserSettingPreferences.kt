/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.andannn.aniflow.datastore.model.PreferencesKeyName
import me.andannn.aniflow.datastore.model.UserSettingPref

class UserSettingPreferences(
    private val preferences: DataStore<Preferences>,
) {
    val userData: Flow<UserSettingPref> =
        preferences.data
            .map { preferences ->
                UserSettingPref(
                    authToken =
                        preferences[stringPreferencesKey(PreferencesKeyName.AUTH_TOKEN_KEY_NAME)],
                    authExpiredTimeInSecond =
                        preferences[intPreferencesKey(PreferencesKeyName.AUTH_EXPIRED_TIME_KEY_NAME)],
                    authedUserId =
                        preferences[stringPreferencesKey(PreferencesKeyName.AUTHED_USER_ID_KEY_NAME)],
                )
            }

    suspend fun setAuthToken(
        token: String,
        expiredTime: Long,
    ) {
        preferences.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                this[stringPreferencesKey(PreferencesKeyName.AUTH_TOKEN_KEY_NAME)] = token
                this[intPreferencesKey(PreferencesKeyName.AUTH_EXPIRED_TIME_KEY_NAME)] = expiredTime.toInt()
            }
        }
    }

    suspend fun clearAuthToken() {
        preferences.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                remove(stringPreferencesKey(PreferencesKeyName.AUTH_TOKEN_KEY_NAME))
                remove(intPreferencesKey(PreferencesKeyName.AUTH_EXPIRED_TIME_KEY_NAME))
            }
        }
    }

    suspend fun setAuthedUserId(userId: String) {
        preferences.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                this[stringPreferencesKey(PreferencesKeyName.AUTHED_USER_ID_KEY_NAME)] = userId
            }
        }
    }
}
