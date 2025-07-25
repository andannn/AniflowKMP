package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Staff(
    /**
     * The id of the staff member
     */
    public val id: Int,
    /**
     * The staff images
     */
    public val image: StaffImage?,
    /**
     * The names of the staff member
     */
    public val name: StaffName?,
)

@Serializable
public data class StaffImage(
    /**
     * The person's image of media at its largest size
     */
    public val large: String?,
    /**
     * The person's image of media at medium size
     */
    public val medium: String?,
)

@Serializable
public data class StaffName(
    /**
     * The person's given name
     */
    public val first: String?,
    /**
     * The person's middle name
     */
    public val middle: String?,
    /**
     * The person's surname
     */
    public val last: String?,
    /**
     * The person's first and last name
     */
    public val full: String?,
    /**
     * The person's full name in their native language
     */
    public val native: String?,
)
