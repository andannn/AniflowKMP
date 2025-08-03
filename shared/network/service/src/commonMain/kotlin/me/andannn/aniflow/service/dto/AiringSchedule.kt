/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class AiringSchedule(
    /**
     * The id of the airing schedule item
     */
    public val id: Int,
    /**
     * The time the episode airs at
     */
    public val airingAt: Int,
    /**
     * Seconds until episode starts airing
     */
    public val timeUntilAiring: Int,
    /**
     * The airing episode number
     */
    public val episode: Int,
    /**
     * The associate media id of the airing episode
     */
    public val mediaId: Int,
    /**
     * The associate media of the airing episode
     */
    public val media: Media? = null,
)
