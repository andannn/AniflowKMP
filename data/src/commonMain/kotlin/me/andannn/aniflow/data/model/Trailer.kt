package me.andannn.aniflow.data.model

public data class Trailer(
    /**
     * The site the video is hosted by (Currently either youtube or dailymotion)
     */
    public val site: String? = null,
    /**
     * The url for the thumbnail image of the video
     */
    public val thumbnail: String? = null,
)
