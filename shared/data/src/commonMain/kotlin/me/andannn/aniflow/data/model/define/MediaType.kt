/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * Media type enum, anime or manga.
 */
@Serializable
enum class MediaType {
    /**
     * Japanese Anime
     */
    ANIME,

    /**
     * Asian comic
     */
    MANGA,

    ;

    fun allCategories(): List<MediaCategory> =
        when (this) {
            ANIME ->
                listOf(
                    MediaCategory.CURRENT_SEASON_ANIME,
                    MediaCategory.NEXT_SEASON_ANIME,
                    MediaCategory.TRENDING_ANIME,
                    MediaCategory.MOVIE_ANIME,
                    MediaCategory.NEW_ADDED_ANIME,
                )

            MANGA ->
                listOf(
                    MediaCategory.TRENDING_MANGA,
                    MediaCategory.ALL_TIME_POPULAR_MANGA,
                    MediaCategory.TOP_MANHWA,
                    MediaCategory.NEW_ADDED_MANGA,
                )
        }
}
