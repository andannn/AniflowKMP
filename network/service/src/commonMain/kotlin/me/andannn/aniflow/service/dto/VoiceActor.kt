package me.andannn.aniflow.service.dto

import kotlin.Double
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class VoiceActor(
  @SerialName(value = "id")
  public val id: Double,
  @SerialName(value = "image")
  public val image: Image,
  @SerialName(value = "name")
  public val name: Name,
)
