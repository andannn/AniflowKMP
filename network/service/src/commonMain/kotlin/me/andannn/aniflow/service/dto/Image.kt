package me.andannn.aniflow.service.dto

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Image(
  @SerialName(value = "large")
  public val large: String,
  @SerialName(value = "medium")
  public val medium: String,
)
