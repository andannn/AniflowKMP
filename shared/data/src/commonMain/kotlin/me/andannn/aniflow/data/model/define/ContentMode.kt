/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

@Serializable
enum class MediaContentMode(
    override val key: String,
) : StringKeyEnum {
    ANIME("ANIME"),
    MANGA("MANGA"),
}

fun MediaContentMode.toMediaType(): MediaType =
    when (this) {
        MediaContentMode.ANIME -> MediaType.ANIME
        MediaContentMode.MANGA -> MediaType.MANGA
    }
