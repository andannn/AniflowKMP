/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.UserStaffNameLanguage

public data class StaffCharacterName(
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
    /**
     * Other names the staff member might be referred to as (pen names)
     */
    public val alternative: List<String> = emptyList(),
)

fun StaffCharacterName?.getNameString(staffName: UserStaffNameLanguage): String {
    if (this == null) return ""

    val romajiWestern =
        listOfNotNull(first, middle, last)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" ")
            ?.ifBlank { full }

    val romaji =
        listOfNotNull(last, first, middle)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" ")
            ?.ifBlank { full }

    return when (staffName) {
        UserStaffNameLanguage.ROMAJI_WESTERN -> {
            romajiWestern ?: romaji ?: native
        }

        UserStaffNameLanguage.ROMAJI -> {
            romaji ?: romajiWestern ?: native
        }

        UserStaffNameLanguage.NATIVE -> {
            native ?: romaji ?: romajiWestern
        }
    } ?: ""
}
