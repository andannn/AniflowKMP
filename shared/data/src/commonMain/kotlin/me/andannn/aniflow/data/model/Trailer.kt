/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

public data class Trailer(
    public val id: String? = null,
    /**
     * The site the video is hosted by (Currently either youtube or dailymotion)
     */
    public val site: String? = null,
    /**
     * The url for the thumbnail image of the video
     */
    public val thumbnail: String? = null,
)

fun Trailer.launchUri(): String? {
    if (id == null || site == null) {
        return null
    }

    val baseUri =
        when (site.lowercase()) {
            "youtube" -> "https://www.youtube.com/watch?v="
            "dailymotion" -> "https://www.dailymotion.com/video/"
            else -> ""
        }
    return baseUri + id
}
