/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import kotlinx.serialization.Serializable

@Serializable
public data class SimpleDate(
    /**
     * Numeric Year (2017)
     */
    public val year: Int,
    /**
     * Numeric Month (3)
     */
    public val month: Int,
    /**
     * Numeric Day (24)
     */
    public val day: Int? = null,
)
