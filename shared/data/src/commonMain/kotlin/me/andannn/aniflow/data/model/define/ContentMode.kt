/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

@Serializable
enum class ContentMode {
    ANIME,
    MANGA,
}

fun ContentMode.toMediaType(): MediaType =
    when (this) {
        ContentMode.ANIME -> MediaType.ANIME
        ContentMode.MANGA -> MediaType.MANGA
    }
