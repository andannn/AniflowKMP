/*
 * Copyright 2025, the AozoraBooks project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.datastore.model

data class UserSettingPref(
    val authToken: String?,
    val authExpiredTimeInSecond: Int?,
    val authedUserId: String?,
)
