/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

import kotlinx.serialization.Serializable
import kotlin.String

/**
 * The type of ranking
 */
@Serializable
enum class MediaRankType(
    public val rawValue: String,
) {
    /**
     * Ranking is based on the media's ratings/score
     */
    RATED("RATED"),

    /**
     * Ranking is based on the media's popularity
     */
    POPULAR("POPULAR"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
