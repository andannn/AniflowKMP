package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Character(
    /**
     * The id of the character
     */
    public val id: Int,
    /**
     * Character images
     */
    public val image: CharacterImage?,
    /**
     * The names of the character
     */
    public val name: CharacterName?,
)

@Serializable
public data class CharacterImage(
    /**
     * The character's image of media at its largest size
     */
    public val large: String?,
    /**
     * The character's image of media at medium size
     */
    public val medium: String?,
)

@Serializable
public data class CharacterName(
    /**
     * The character's given name
     */
    public val first: String?,
    /**
     * The character's middle name
     */
    public val middle: String?,
    /**
     * The character's surname
     */
    public val last: String?,
    /**
     * The character's first and last name
     */
    public val full: String?,
    /**
     * The character's full name in their native language
     */
    public val native: String?,
)
