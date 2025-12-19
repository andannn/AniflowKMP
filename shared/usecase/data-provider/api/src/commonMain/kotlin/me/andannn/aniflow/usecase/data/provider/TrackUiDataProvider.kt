/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

data class TrackUiState(
    private val items: List<MediaWithMediaListItem> = emptyList(),
    val authedUser: UserModel? = null,
    val userOptions: UserOptions = UserOptions.Default,
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
                CategoryWithItems(TrackCategory.NEXT_UP, nextItems),
                CategoryWithItems(TrackCategory.UPCOMING, upcomingItems),
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

/**
 * Provides data for the Track UI components.
 */
interface TrackUiDataProvider : DataProvider {
    @NativeCoroutines
    fun uiDataFlow(): Flow<TrackUiState>
}
