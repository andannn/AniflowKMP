/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * Media sort enums
 */
@Serializable
enum class MediaSort(
    override val key: String,
) : StringKeyEnum {
    START_DATE("START_DATE"),
    START_DATE_DESC("START_DATE_DESC"),
    POPULARITY_DESC("POPULARITY_DESC"),
    TRENDING_DESC("TRENDING_DESC"),
    FAVOURITES_DESC("FAVOURITES_DESC"),
}
