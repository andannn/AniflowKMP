package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.ExternalLinkType

@Serializable
public data class ExternalLink(
    /**
     * The id of the external link
     */
    public val id: Int,
    /**
     * The url of the external link or base url of link source
     */
    public val url: String?,
    /**
     * The links website site name
     */
    public val site: String,
    public val type: ExternalLinkType?,
    /**
     * The links website site id
     */
    public val siteId: Int?,
    public val color: String?,
    /**
     * The icon image url of the site. Not available for all links. Transparent PNG 64x64
     */
    public val icon: String?,
)
