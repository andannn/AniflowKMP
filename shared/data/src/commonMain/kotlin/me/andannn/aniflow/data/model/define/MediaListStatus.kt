/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * Media list watching/reading status enum.
 */
@Serializable
enum class MediaListStatus(
    override val key: String,
) : StringKeyEnum {
    /**
     * Currently watching/reading
     */
    CURRENT("CURRENT"),

    /**
     * Planning to watch/read
     */
    PLANNING("PLANNING"),

    /**
     * Finished watching/reading
     */
    COMPLETED("COMPLETED"),

    /**
     * Stopped watching/reading before completing
     */
    DROPPED("DROPPED"),

    /**
     * Paused watching/reading
     */
    PAUSED("PAUSED"),

    /**
     * Re-watching/reading
     */
    REPEATING("REPEATING"),
}
