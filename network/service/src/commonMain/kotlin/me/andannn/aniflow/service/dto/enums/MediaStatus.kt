
package me.andannn.aniflow.service.dto.enums

/**
 * The current releasing status of the media
 */
enum class MediaStatus(
    public val rawValue: String,
) {
    /**
     * Has completed and is no longer being released
     */
    FINISHED("FINISHED"),

    /**
     * Currently releasing
     */
    RELEASING("RELEASING"),

    /**
     * To be released at a later date
     */
    NOT_YET_RELEASED("NOT_YET_RELEASED"),

    /**
     * Ended before the work could be finished
     */
    CANCELLED("CANCELLED"),

    /**
     * Version 2 only. Is currently paused from releasing and will resume at a later date
     */
    HIATUS("HIATUS"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
