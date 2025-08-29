/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * The type of ranking
 */
@Serializable
enum class MediaRankType(
    override val key: String,
) : StringKeyEnum {
    /**
     * Ranking is based on the media's ratings/score
     */
    RATED("RATING"),

    /**
     * Ranking is based on the media's popularity
     */
    POPULAR("POPULAR"),
}
