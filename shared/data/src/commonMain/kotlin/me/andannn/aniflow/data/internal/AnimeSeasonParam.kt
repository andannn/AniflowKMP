/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import me.andannn.aniflow.data.model.define.MediaSeason
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal data class AnimeSeasonParam(
    val seasonYear: Int,
    val season: MediaSeason,
) : Comparable<AnimeSeasonParam> {
    override fun compareTo(other: AnimeSeasonParam): Int =
        when {
            this.seasonYear != other.seasonYear -> this.seasonYear - other.seasonYear
            this.season != other.season -> this.season.ordinal - other.season.ordinal
            else -> 0
        }
}

internal fun AnimeSeasonParam.nextSeasonParam(): AnimeSeasonParam {
    val (nextSeasonYear, nextSeason) =
        when (this.season) {
            MediaSeason.WINTER -> seasonYear to MediaSeason.SPRING
            MediaSeason.SPRING -> seasonYear to MediaSeason.SUMMER
            MediaSeason.SUMMER -> seasonYear to MediaSeason.FALL
            MediaSeason.FALL -> (seasonYear + 1) to MediaSeason.WINTER
        }
    return AnimeSeasonParam(nextSeasonYear, nextSeason)
}

@OptIn(ExperimentalTime::class)
internal fun currentSeasonByLocalDataTime(): AnimeSeasonParam {
    val currentLocalDatetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val year = currentLocalDatetime.year
    val month = currentLocalDatetime.month.number

    val season =
        when (month) {
            in 1..3 -> MediaSeason.WINTER
            in 4..6 -> MediaSeason.SPRING
            in 7..9 -> MediaSeason.SUMMER
            in 10..12 -> MediaSeason.FALL
            else -> throw IllegalStateException("Impossible month: $month")
        }

    return AnimeSeasonParam(seasonYear = year, season = season)
}
