/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

/**
 * The role the character plays in the media
 */
enum class CharacterRole(
    override val key: String,
) : StringKeyEnum {
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
}
