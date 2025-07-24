package me.andannn.aniflow.service.dto

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Name(
  @SerialName(value = "first")
  public val first: String,
  @SerialName(value = "last")
  public val last: String,
  @SerialName(value = "full")
  public val full: String,
  @SerialName(value = "native")
  public val native: String,
)
