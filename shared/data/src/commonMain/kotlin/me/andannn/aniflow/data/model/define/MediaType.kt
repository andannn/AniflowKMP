/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * Media type enum, anime or manga.
 */
@Serializable
enum class MediaType {
    /**
     * Japanese Anime
     */
    ANIME,

    /**
     * Asian comic
     */
    MANGA,
}
