/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

data class DiscoverUiState(
    val categoryDataMap: CategoryDataModel = CategoryDataModel(),
    val newReleasedMedia: List<MediaWithMediaListItem> = emptyList(),
) {
    companion object {
        val Empty = DiscoverUiState()
    }
}

data class TrackUiState(
    private val items: List<MediaWithMediaListItem> = emptyList(),
) {
    companion object {
        val Empty = TrackUiState()
    }

    private val newItems: List<MediaWithMediaListItem> =
        items
            .filter {
                it.isNewReleased
            }.sortedByDescending { it.mediaListModel.updatedAt }

    private val nextItems: List<MediaWithMediaListItem> =
        items
            .filter {
                it.haveNextEpisode && !it.isNewReleased
            }.sortedByDescending { it.mediaListModel.updatedAt }

    private val otherItems: List<MediaWithMediaListItem> =
        items
            .filter {
                !it.haveNextEpisode && !it.isNewReleased
            }.sortedByDescending { it.mediaListModel.updatedAt }

    val categoryWithItems =
        listOf(
            TrackCategory.NEW_RELEASED to newItems,
            TrackCategory.NEXT_UP to nextItems,
            TrackCategory.OTHER to otherItems,
        )

    enum class TrackCategory(
        val title: String,
    ) {
        NEW_RELEASED("New Released"),
        NEXT_UP("Next Up"),
        OTHER("Other"),
    }
}

data class HomeAppBarUiState(
    val authedUser: UserModel? = null,
    val contentMode: MediaContentMode = MediaContentMode.ANIME,
) {
    companion object {
        val Empty = HomeAppBarUiState()
    }
}
