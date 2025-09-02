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

    data class CategoryWithItems(
        val category: TrackCategory,
        val items: List<MediaWithMediaListItem>,
    )

    val categoryWithItems: List<CategoryWithItems>

    init {
        val newItems = mutableListOf<MediaWithMediaListItem>()

        val nextItems = mutableListOf<MediaWithMediaListItem>()

        val upcomingItems = mutableListOf<MediaWithMediaListItem>()

        val otherItems = mutableListOf<MediaWithMediaListItem>()

        items.forEach {
            if (it.isNewReleased) {
                newItems.add(it)
            } else if (it.haveNextEpisode) {
                nextItems.add(it)
            } else if (it.hasReleaseInfo) {
                upcomingItems.add(it)
            } else {
                otherItems.add(it)
            }
        }

        categoryWithItems =
            listOf(
                CategoryWithItems(TrackCategory.NEW_RELEASED, newItems),
                CategoryWithItems(TrackCategory.UPCOMING, upcomingItems),
                CategoryWithItems(TrackCategory.NEXT_UP, nextItems),
                CategoryWithItems(TrackCategory.OTHER, otherItems),
            )
    }

    enum class TrackCategory(
        val title: String,
    ) {
        /**
         * 最近更新（有下一集且三天内更新过）
         */
        NEW_RELEASED("New Released"),

        /**
         * 下一集（有下一集但三天内没有更新过）
         */
        NEXT_UP("Next Up"),

        /**
         * 没有下一集， 有下一集的Release时间
         */
        UPCOMING("Upcoming"),

        /**
         * 其他
         */
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
