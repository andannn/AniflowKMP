/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * The primary language of the voice actor
 */
@Serializable
enum class StaffLanguage {
    /**
     * Japanese
     */
    JAPANESE,

    /**
     * English
     */
    ENGLISH,

    /**
     * Korean
     */
    KOREAN,

    /**
     * Italian
     */
    ITALIAN,

    /**
     * Spanish
     */
    SPANISH,

    /**
     * Portuguese
     */
    PORTUGUESE,

    /**
     * French
     */
    FRENCH,

    /**
     * German
     */
    GERMAN,

    /**
     * Hebrew
     */
    HEBREW,

    /**
     * Hungarian
     */
    HUNGARIAN,
}
