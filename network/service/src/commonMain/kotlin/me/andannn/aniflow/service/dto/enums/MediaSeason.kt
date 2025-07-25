
package me.andannn.aniflow.service.dto.enums

import kotlin.Array
import kotlin.Deprecated
import kotlin.String
import kotlin.collections.List

enum class MediaSeason(
  public val rawValue: String,
) {
  /**
   * Months December to February
   */
  WINTER("WINTER"),
  /**
   * Months March to May
   */
  SPRING("SPRING"),
  /**
   * Months June to August
   */
  SUMMER("SUMMER"),
  /**
   * Months September to November
   */
  FALL("FALL"),
  /**
   * Auto generated constant for unknown enum values
   */
  UNKNOWN__("UNKNOWN__"),
  ;
}
