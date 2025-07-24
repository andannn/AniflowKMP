package me.andannn.aniflow.service.dto

import kotlin.Double
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ExternalLink(
  @SerialName(value = "id")
  public val id: Double,
  @SerialName(value = "url")
  public val url: String,
  @SerialName(value = "site")
  public val site: String,
  @SerialName(value = "type")
  public val type: String,
  @SerialName(value = "siteId")
  public val siteId: Double,
  @SerialName(value = "color")
  public val color: String,
  @SerialName(value = "icon")
  public val icon: String,
)
