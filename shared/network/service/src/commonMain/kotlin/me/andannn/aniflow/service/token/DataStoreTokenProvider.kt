/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.token

import kotlinx.coroutines.flow.firstOrNull
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.TokenProvider

class DataStoreTokenProvider(
    private val settingPref: UserSettingPreferences,
) : TokenProvider {
    override suspend fun getAccessToken(): String? = settingPref.userData.firstOrNull()?.authToken
}
