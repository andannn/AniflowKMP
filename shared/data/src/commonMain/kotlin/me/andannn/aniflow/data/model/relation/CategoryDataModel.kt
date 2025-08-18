/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory

data class CategoryDataModel(
    private val map: List<CategoryWithContents> = emptyList(),
) {
    val content: List<CategoryWithContents>
        get() =
            map
                .toList()
                .sortedBy {
                    it.category.orderIndex()
                }
}

private fun MediaCategory.orderIndex(): Int =
    when (this) {
        MediaCategory.CURRENT_SEASON_ANIME -> 0
        MediaCategory.NEXT_SEASON_ANIME -> 1
        MediaCategory.TRENDING_ANIME -> 2
        MediaCategory.MOVIE_ANIME -> 3
        MediaCategory.NEW_ADDED_ANIME -> 4
        MediaCategory.TRENDING_MANGA -> 5
        MediaCategory.ALL_TIME_POPULAR_MANGA -> 6
        MediaCategory.TOP_MANHWA -> 7
        MediaCategory.NEW_ADDED_MANGA -> 8
    }

data class CategoryWithContents(
    val category: MediaCategory,
    val medias: List<MediaModel>,
)
