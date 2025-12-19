/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

enum class MediaCategory(
    override val key: String,
) : StringKeyEnum {
    // current season releasing anime.
    CURRENT_SEASON_ANIME("CURRENT_SEASON_ANIME"),

    // next season not yet released anime.
    NEXT_SEASON_ANIME("NEXT_SEASON_ANIME"),

    // now trending anime.
    TRENDING_ANIME("TRENDING_ANIME"),

    // popular movie.
    MOVIE_ANIME("MOVIE_ANIME"),

    // trending Manga.
    TRENDING_MANGA("TRENDING_MANGA"),

    // all time popular manga.
    ALL_TIME_POPULAR_MANGA("ALL_TIME_POPULAR_MANGA"),

    // South korea top.
    TOP_MANHWA("TOP_MANHWA"),

    // New added anime
    NEW_ADDED_ANIME("NEW_ADDED_ANIME"),

    // New added manga
    NEW_ADDED_MANGA("NEW_ADDED_MANGA"),
}
