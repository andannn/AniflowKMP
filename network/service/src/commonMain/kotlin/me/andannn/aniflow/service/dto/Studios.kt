package me.andannn.aniflow.service.dto

import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Studios(
  @SerialName(value = "nodes")
  public val nodes: List<Node>,
)
