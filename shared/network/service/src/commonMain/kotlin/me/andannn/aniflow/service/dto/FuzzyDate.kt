/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class FuzzyDate(
    /**
     * Numeric Year (2017)
     */
    public val year: Int? = null,
    /**
     * Numeric Month (3)
     */
    public val month: Int? = null,
    /**
     * Numeric Day (24)
     */
    public val day: Int? = null,
)
