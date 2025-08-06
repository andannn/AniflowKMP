/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel

data class MediaWithMediaListItem(
    val mediaModel: MediaModel,
    val mediaListModel: MediaListModel,
)
