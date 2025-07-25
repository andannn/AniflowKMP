package me.andannn.aniflow.service.dto.enums

import kotlin.String

/**
 * Media list scoring type
 */
enum class ScoreFormat(
    public val rawValue: String,
) {
    /**
     * An integer from 0-100
     */
    POINT_100("POINT_100"),

    /**
     * A float from 0-10 with 1 decimal place
     */
    POINT_10_DECIMAL("POINT_10_DECIMAL"),

    /**
     * An integer from 0-10
     */
    POINT_10("POINT_10"),

    /**
     * An integer from 0-5. Should be represented in Stars
     */
    POINT_5("POINT_5"),

    /**
     * An integer from 0-3. Should be represented in Smileys. 0 => No Score, 1 => :(, 2 => :|, 3 => :)
     */
    POINT_3("POINT_3"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
