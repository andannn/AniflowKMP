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
     * Character images
     */
    public val image: CharacterImage? = null,
    /**
     * The names of the character
     */
    public val name: CharacterName? = null,
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
