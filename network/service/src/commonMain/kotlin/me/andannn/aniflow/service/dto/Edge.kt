package me.andannn.aniflow.service.dto

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Edge(
  @SerialName(value = "role")
  public val role: String,
  @SerialName(value = "node")
  public val node: Node,
)
