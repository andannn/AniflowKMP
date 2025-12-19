/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

enum class MediaSeason(
    override val key: String,
) : StringKeyEnum {
    /**
     * Months December to February
     */
    WINTER("WINTER"),

    /**
     * Months March to May
     */
    SPRING("SPRING"),

    /**
     * Months June to August
     */
    SUMMER("SUMMER"),

    /**
     * Months September to November
     */
    FALL("FALL"),
}
