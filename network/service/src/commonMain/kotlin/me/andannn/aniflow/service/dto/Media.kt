package me.andannn.aniflow.service.dto

import kotlin.Boolean
import kotlin.Double
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Media(
  @SerialName(value = "id")
  public val id: Double,
  @SerialName(value = "title")
  public val title: Title,
  @SerialName(value = "type")
  public val type: String,
  @SerialName(value = "description")
  public val description: String,
  @SerialName(value = "episodes")
  public val episodes: Double,
  @SerialName(value = "seasonYear")
  public val seasonYear: Double,
  @SerialName(value = "season")
  public val season: String,
  @SerialName(value = "source")
  public val source: String,
  @SerialName(value = "genres")
  public val genres: List<String>,
  @SerialName(value = "status")
  public val status: String,
  @SerialName(value = "isFavourite")
  public val isFavourite: Boolean,
  @SerialName(value = "externalLinks")
  public val externalLinks: List<ExternalLink>,
  @SerialName(value = "rankings")
  public val rankings: List<Ranking>,
  @SerialName(value = "trailer")
  public val trailer: Trailer,
  @SerialName(value = "coverImage")
  public val coverImage: CoverImage,
  @SerialName(value = "format")
  public val format: String,
  @SerialName(value = "bannerImage")
  public val bannerImage: String,
  @SerialName(value = "averageScore")
  public val averageScore: Double,
  @SerialName(value = "favourites")
  public val favourites: Double,
  @SerialName(value = "trending")
  public val trending: Double,
  @SerialName(value = "characters")
  public val characters: Characters,
  @SerialName(value = "staff")
  public val staff: Staff,
  @SerialName(value = "studios")
  public val studios: Studios,
)
