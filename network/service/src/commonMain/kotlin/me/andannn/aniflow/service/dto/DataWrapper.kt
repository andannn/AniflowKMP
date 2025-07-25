package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DataWrapper<T>(
  @SerialName(value = "data")
  public val `data`: T,
)
