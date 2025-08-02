/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

data class EpisodeModel(
    /**
     * The airing episode number
     */
    public val episode: Int? = null,
    /**
     * Seconds until episode starts airing
     */
    public val timeUntilAiring: Int? = null,
)
