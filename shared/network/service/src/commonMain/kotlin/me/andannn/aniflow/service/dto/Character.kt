/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Character(
    /**
     * The id of the character
     */
    public val id: Int,
    /**
     * The names of the character
     */
    public val name: CharacterName? = null,
    /**
     * Character images
     */
    public val image: CharacterImage? = null,
    /**
     * A general description of the character
     */
    public val description: String? = null,
    /**
     * The character's gender. Usually Male, Female, or Non-binary but can be any string.
     */
    public val gender: String? = null,
    /**
     * The character's birth date
     */
    public val dateOfBirth: FuzzyDate? = null,
    /**
     * The character's age. Note this is a string, not an int, it may contain further text and
     * additional ages.
     */
    public val age: String? = null,
    /**
     * The characters blood type
     */
    public val bloodType: String? = null,
    /**
     * If the character is marked as favourite by the currently authenticated user
     */
    public val isFavourite: Boolean? = null,
    /**
     * The url for the character page on the AniList website
     */
    public val siteUrl: String? = null,
    /**
     * The amount of user's who have favourited the character
     */
    public val favourites: Int? = null,
    /**
     * Media that includes the character
     */
    public val media: MediaConnection? = null,
)

@Serializable
public data class CharacterImage(
    /**
     * The character's image of media at its largest size
     */
    public val large: String? = null,
    /**
     * The character's image of media at medium size
     */
    public val medium: String? = null,
)

@Serializable
public data class CharacterName(
    /**
     * The character's given name
     */
    public val first: String? = null,
    /**
     * The character's middle name
     */
    public val middle: String? = null,
    /**
     * The character's surname
     */
    public val last: String? = null,
    /**
     * The character's first and last name
     */
    public val full: String? = null,
    /**
     * The character's full name in their native language
     */
    public val native: String? = null,
)
