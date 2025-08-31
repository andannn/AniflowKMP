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
enum class StaffLanguage(
    override val key: String,
) : StringKeyEnum {
    /**
     * Japanese
     */
    JAPANESE("JAPANESE"),

    /**
     * English
     */
    ENGLISH("ENGLISH"),

    /**
     * Korean
     */
    KOREAN("KOREAN"),

    /**
     * Italian
     */
    ITALIAN("ITALIAN"),

    /**
     * Spanish
     */
    SPANISH("SPANISH"),

    /**
     * Portuguese
     */
    PORTUGUESE("PORTUGUESE"),

    /**
     * French
     */
    FRENCH("FRENCH"),

    /**
     * German
     */
    GERMAN("GERMAN"),

    /**
     * Hebrew
     */
    HEBREW("HEBREW"),

    /**
     * Hungarian
     */
    HUNGARIAN("HUNGARIAN"),
}
