package me.andannn.aniflow.service.dto

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Title(
  @SerialName(value = "romaji")
  public val romaji: String,
  @SerialName(value = "english")
  public val english: String,
  @SerialName(value = "native")
  public val native: String,
)
