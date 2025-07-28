/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

enum class ExternalLinkType(
    public val rawValue: String,
) {
    INFO("INFO"),
    STREAMING("STREAMING"),
    SOCIAL("SOCIAL"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
