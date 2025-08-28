/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.datastore.model

data class UserSettingPref(
    val authToken: String?,
    val authExpiredTimeInSecond: Int?,
    val authedUserId: String?,
    val contentMode: String?,
    val titleLanguage: String?,
    val staffNameLanguage: String?,
    val sentNotificationIds: List<String>,
)
