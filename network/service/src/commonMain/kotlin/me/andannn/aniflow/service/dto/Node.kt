package me.andannn.aniflow.service.dto

import kotlin.Boolean
import kotlin.Double
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Node(
  @SerialName(value = "id")
  public val id: Double,
  @SerialName(value = "name")
  public val name: String,
  @SerialName(value = "isAnimationStudio")
  public val isAnimationStudio: Boolean,
  @SerialName(value = "siteUrl")
  public val siteUrl: String,
  @SerialName(value = "isFavourite")
  public val isFavourite: Boolean,
)
