/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

import kotlinx.serialization.Serializable
import kotlin.String

/**
 * The role the character plays in the media
 */
@Serializable
enum class CharacterRole(
    public val rawValue: String,
) {
    /**
     * A primary character role in the media
     */
    MAIN("MAIN"),

    /**
     * A supporting character role in the media
     */
    SUPPORTING("SUPPORTING"),

    /**
     * A background character in the media
     */
    BACKGROUND("BACKGROUND"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
