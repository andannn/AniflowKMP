/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

/**
 * Media type enum, anime or manga.
 */
enum class MediaType(
    override val key: String,
) : StringKeyEnum {
    /**
     * Japanese Anime
     */
    ANIME("ANIME"),

    /**
     * Asian comic
     */
    MANGA("MANGA"),

    ;

    fun allCategories(): List<MediaCategory> =
        when (this) {
            ANIME -> {
                listOf(
                    MediaCategory.CURRENT_SEASON_ANIME,
                    MediaCategory.NEXT_SEASON_ANIME,
                    MediaCategory.TRENDING_ANIME,
                    MediaCategory.MOVIE_ANIME,
                    MediaCategory.NEW_ADDED_ANIME,
                )
            }

            MANGA -> {
                listOf(
                    MediaCategory.TRENDING_MANGA,
                    MediaCategory.ALL_TIME_POPULAR_MANGA,
                    MediaCategory.TOP_MANHWA,
                    MediaCategory.NEW_ADDED_MANGA,
                )
            }
        }
}
