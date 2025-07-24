package me.andannn.aniflow.service.dto

import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Staff(
  @SerialName(value = "pageInfo")
  public val pageInfo: PageInfo,
  @SerialName(value = "edges")
  public val edges: List<Edge>,
)
