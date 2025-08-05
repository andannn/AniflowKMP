/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

data class UserModel(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val bannerImage: String? = null,
    val unreadNotificationCount: Int = 0,
)
