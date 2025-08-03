/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * The role the character plays in the media
 */
@Serializable
enum class CharacterRole {
    /**
     * A primary character role in the media
     */
    MAIN,

    /**
     * A supporting character role in the media
     */
    SUPPORTING,

    /**
     * A background character in the media
     */
    BACKGROUND,
}
