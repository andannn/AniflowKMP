package me.andannn.aniflow.service.dto

import kotlin.Boolean
import kotlin.Double
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Ranking(
  @SerialName(value = "rank")
  public val rank: Double,
  @SerialName(value = "type")
  public val type: String,
  @SerialName(value = "allTime")
  public val allTime: Boolean,
)
