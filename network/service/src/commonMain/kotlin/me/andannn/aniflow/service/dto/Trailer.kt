package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Trailer(
    /**
     * The trailer video id
     */
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
