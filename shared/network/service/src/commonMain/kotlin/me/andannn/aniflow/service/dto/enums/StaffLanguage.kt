/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

/**
 * The primary language of the voice actor
 */
enum class StaffLanguage(
    public val rawValue: String,
) {
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

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
