/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.model.define.MediaCategory

@Serializable
sealed interface Screen {
    @Serializable
    object Home : Screen

    @Serializable
    data class MediaCategoryList(
        val category: MediaCategory,
    ) : Screen
}
