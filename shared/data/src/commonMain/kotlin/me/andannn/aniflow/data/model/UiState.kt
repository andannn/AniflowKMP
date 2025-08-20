/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.ContentMode
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

data class DiscoverUiState(
    val categoryDataMap: CategoryDataModel = CategoryDataModel(),
    val authedUser: UserModel? = null,
    val contentMode: ContentMode = ContentMode.ANIME,
) {
    companion object {
        val Empty = DiscoverUiState()
    }
}

data class TrackUiState(
    val items: List<MediaWithMediaListItem> = emptyList(),
) {
    companion object {
        val Empty = TrackUiState()
    }
}
