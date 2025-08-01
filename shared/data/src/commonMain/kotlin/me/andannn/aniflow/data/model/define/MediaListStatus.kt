/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

/**
 * Media list watching/reading status enum.
 */
enum class MediaListStatus {
    /**
     * Currently watching/reading
     */
    CURRENT,

    /**
     * Planning to watch/read
     */
    PLANNING,

    /**
     * Finished watching/reading
     */
    COMPLETED,

    /**
     * Stopped watching/reading before completing
     */
    DROPPED,

    /**
     * Paused watching/reading
     */
    PAUSED,

    /**
     * Re-watching/reading
     */
    REPEATING,
}
