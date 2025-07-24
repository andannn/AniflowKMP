package me.andannn.aniflow.service.dto

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Trailer(
  @SerialName(value = "id")
  public val id: String,
  @SerialName(value = "site")
  public val site: String,
  @SerialName(value = "thumbnail")
  public val thumbnail: String,
)
