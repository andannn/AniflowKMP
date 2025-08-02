/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

data class CharacterModel(
    /**
     * The id of the character
     */
    public val id: Int,
    /**
     * The names of the character
     */
    public val name: StaffCharacterName? = null,
    /**
     * Character images
     */
    public val image: String? = null,
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
    public val dateOfBirth: SimpleDate? = null,
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
)
