/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

public data class StaffModel(
    /**
     * The id of the staff member
     */
    public val id: Int,
    /**
     * The names of the staff member
     */
    public val name: StaffCharacterName? = null,
    /**
     * The staff images
     */
    public val image: String? = null,
    /**
     * A general description of the staff member
     */
    public val description: String? = null,
    /**
     * The staff's gender. Usually Male, Female, or Non-binary but can be any string.
     */
    public val gender: String? = null,
    public val dateOfBirth: SimpleDate? = null,
    public val dateOfDeath: SimpleDate? = null,
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
)
