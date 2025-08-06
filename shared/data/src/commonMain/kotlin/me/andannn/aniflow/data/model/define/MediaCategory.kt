/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

@Serializable
enum class MediaCategory {
    // current season releasing anime.
    CURRENT_SEASON_ANIME,

    // next season not yet released anime.
    NEXT_SEASON_ANIME,

    // now trending anime.
    TRENDING_ANIME,

    // popular movie.
    MOVIE_ANIME,

    // trending Manga.
    TRENDING_MANGA,

    // all time popular manga.
    ALL_TIME_POPULAR_MANGA,

    // South korea top.
    TOP_MANHWA,

    // New added anime
    NEW_ADDED_ANIME,

    // New added manga
    NEW_ADDED_MANGA,
}
