/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

import kotlinx.serialization.Serializable

/**
 * Activity type enum.
 */
@Serializable
enum class ActivityType(
    public val rawValue: String,
) {
    /**
     * A text activity
     */
    TEXT("TEXT"),

    /**
     * A anime list update activity
     */
    ANIME_LIST("ANIME_LIST"),

    /**
     * A manga list update activity
     */
    MANGA_LIST("MANGA_LIST"),

    /**
     * A text message activity sent to another user
     */
    MESSAGE("MESSAGE"),

    /**
     * Anime & Manga list update, only used in query arguments
     */
    MEDIA_LIST("MEDIA_LIST"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
