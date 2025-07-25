package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Trailer(
    /**
     * The trailer video id
     */
    public val id: String?,
    /**
     * The site the video is hosted by (Currently either youtube or dailymotion)
     */
    public val site: String?,
    /**
     * The url for the thumbnail image of the video
     */
    public val thumbnail: String?,
)
