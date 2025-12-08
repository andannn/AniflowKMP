/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.util

import me.andannn.aniflow.data.model.MediaModel

object SharedElementKey {
    fun keyOfMediaItem(item: MediaModel) = "cover_preview_item_$item"
}
