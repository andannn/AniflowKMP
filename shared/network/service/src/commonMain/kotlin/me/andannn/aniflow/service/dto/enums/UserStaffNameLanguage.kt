/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

import kotlin.String

/**
 * The language the user wants to see staff and character names in
 */
enum class UserStaffNameLanguage(
    public val rawValue: String,
) {
    /**
     * The romanization of the staff or character's native name, with western name ordering
     */
    ROMAJI_WESTERN("ROMAJI_WESTERN"),

    /**
     * The romanization of the staff or character's native name
     */
    ROMAJI("ROMAJI"),

    /**
     * The staff or character's name in their native language
     */
    NATIVE("NATIVE"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
