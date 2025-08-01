/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.MediaRankType

@Serializable
public data class Ranking(
    /**
     * The numerical rank of the media
     */
    public val rank: Int,
    /**
     * The type of ranking
     */
    public val type: MediaRankType,
    /**
     * If the ranking is based on all time instead of a season/year
     */
    public val allTime: Boolean? = null,
)
