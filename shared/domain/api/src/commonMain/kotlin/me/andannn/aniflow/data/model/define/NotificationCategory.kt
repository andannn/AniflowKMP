/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

enum class NotificationCategory(
    override val key: String,
) : StringKeyEnum {
    ALL("ALL"),
    AIRING("AIRING"),
    ACTIVITY("ACTIVITY"),
    FOLLOWS("FOLLOWS"),
    MEDIA("MEDIA"),
}
