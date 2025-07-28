package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class CoverImage(
    /**
     * The cover image url of the media at its largest size. If this size isn't available, large
     * will be provided instead.
     */
    public val extraLarge: String? = null,
    /**
     * The cover image url of the media at a large size
     */
    public val large: String? = null,
    /**
     * The cover image url of the media at medium size
     */
    public val medium: String? = null,
    /**
     * Average #hex color of cover image
     */
    public val color: String? = null,
)
