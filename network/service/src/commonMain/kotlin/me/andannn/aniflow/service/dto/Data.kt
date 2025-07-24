package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Data(
  @SerialName(value = "Media")
  public val Media: Media,
)
