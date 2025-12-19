/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

/**
 * Media list scoring type
 */
enum class ScoreFormat(
    override val key: String,
) : StringKeyEnum {
    /**
     * An integer from 0-100
     */
    POINT_100("point_100"),

    /**
     * A float from 0-10 with 1 decimal place
     */
    POINT_10_DECIMAL("point_10_decimal"),

    /**
     * An integer from 0-10
     */
    POINT_10("point_10"),

    /**
     * An integer from 0-5. Should be represented in Stars
     */
    POINT_5("point_5"),

    /**
     * An integer from 0-3. Should be represented in Smileys. 0 => No Score, 1 => :(, 2 => :|, 3 => :)
     */
    POINT_3("point_3"),
}
