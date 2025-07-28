/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

/**
 * Media type enum, anime or manga.
 */
enum class MediaType(
    public val rawValue: String,
) {
    /**
     * Japanese Anime
     */
    ANIME("ANIME"),

    /**
     * Asian comic
     */
    MANGA("MANGA"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
