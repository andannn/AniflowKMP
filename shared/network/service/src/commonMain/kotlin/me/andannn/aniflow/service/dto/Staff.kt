/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Staff(
    /**
     * The id of the staff member
     */
    public val id: Int,
    /**
     * The names of the staff member
     */
    public val name: StaffName? = null,
    /**
     * The staff images
     */
    public val image: StaffImage? = null,
    /**
     * A general description of the staff member
     */
    public val description: String? = null,
    /**
     * The staff's gender. Usually Male, Female, or Non-binary but can be any string.
     */
    public val gender: String? = null,
    public val dateOfBirth: FuzzyDate? = null,
    public val dateOfDeath: FuzzyDate? = null,
    /**
     * The person's age in years
     */
    public val age: Int? = null,
    /**
     * [startYear, endYear] (If the 2nd value is not present staff is still active)
     */
    public val yearsActive: List<Int?>? = null,
    /**
     * The persons birthplace or hometown
     */
    public val homeTown: String? = null,
    /**
     * The persons blood type
     */
    public val bloodType: String? = null,
    /**
     * If the staff member is marked as favourite by the currently authenticated user
     */
    public val isFavourite: Boolean? = null,
    /**
     * The url for the staff page on the AniList website
     */
    public val siteUrl: String? = null,
    /**
     * Media the actor voiced characters in. (Same data as characters with media as node instead of
     * characters)
     */
    public val characterMedia: MediaConnection? = null,
)

@Serializable
public data class StaffImage(
    /**
     * The person's image of media at its largest size
     */
    public val large: String? = null,
    /**
     * The person's image of media at medium size
     */
    public val medium: String? = null,
)

@Serializable
public data class StaffName(
    /**
     * The person's given name
     */
    public val first: String? = null,
    /**
     * The person's middle name
     */
    public val middle: String? = null,
    /**
     * The person's surname
     */
    public val last: String? = null,
    /**
     * The person's first and last name
     */
    public val full: String? = null,
    /**
     * The person's full name in their native language
     */
    public val native: String? = null,
    public val alternative: List<String?>? = null,
)
