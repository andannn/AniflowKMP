/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

import kotlin.String

enum class MediaSeason(
    public val rawValue: String,
) {
    /**
     * Months December to February
     */
    WINTER("WINTER"),

    /**
     * Months March to May
     */
    SPRING("SPRING"),

    /**
     * Months June to August
     */
    SUMMER("SUMMER"),

    /**
     * Months September to November
     */
    FALL("FALL"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
