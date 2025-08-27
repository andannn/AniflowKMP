/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * The language the user wants to see staff and character names in
 */
@Serializable
enum class UserStaffNameLanguage {
    /**
     * The romanization of the staff or character's native name, with western name ordering
     */
    ROMAJI_WESTERN,

    /**
     * The romanization of the staff or character's native name
     */
    ROMAJI,

    /**
     * The staff or character's name in their native language
     */
    NATIVE,

    ;

    companion object {
        val Default = NATIVE
    }
}
