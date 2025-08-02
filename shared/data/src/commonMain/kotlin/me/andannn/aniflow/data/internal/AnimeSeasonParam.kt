/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import me.andannn.aniflow.data.model.define.MediaSeason

internal data class AnimeSeasonParam(
    val seasonYear: Int,
    val season: MediaSeason,
) {
    fun getNextSeasonParam(): AnimeSeasonParam =
        when (season) {
            MediaSeason.WINTER -> AnimeSeasonParam(seasonYear, MediaSeason.SPRING)
            MediaSeason.SPRING -> AnimeSeasonParam(seasonYear, MediaSeason.SUMMER)
            MediaSeason.SUMMER -> AnimeSeasonParam(seasonYear, MediaSeason.FALL)
            MediaSeason.FALL -> AnimeSeasonParam(seasonYear + 1, MediaSeason.WINTER)
        }
}
