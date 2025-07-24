package me.andannn.aniflow.service.dto

import kotlin.Boolean
import kotlin.Double
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PageInfo(
  @SerialName(value = "total")
  public val total: Double,
  @SerialName(value = "perPage")
  public val perPage: Double,
  @SerialName(value = "currentPage")
  public val currentPage: Double,
  @SerialName(value = "lastPage")
  public val lastPage: Double,
  @SerialName(value = "hasNextPage")
  public val hasNextPage: Boolean,
)
