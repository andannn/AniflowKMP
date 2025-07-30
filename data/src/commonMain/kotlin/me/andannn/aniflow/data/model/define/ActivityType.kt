/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

/**
 * Activity type enum.
 */
enum class ActivityType {
    /**
     * A text activity
     */
    TEXT,

    /**
     * A anime list update activity
     */
    ANIME_LIST,

    /**
     * A manga list update activity
     */
    MANGA_LIST,

    /**
     * A text message activity sent to another user
     */
    MESSAGE,

    /**
     * Anime & Manga list update, only used in query arguments
     */
    MEDIA_LIST,
}
