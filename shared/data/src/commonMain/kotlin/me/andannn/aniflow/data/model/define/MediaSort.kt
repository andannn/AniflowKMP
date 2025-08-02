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
enum class MediaSort {
    START_DATE,
    START_DATE_DESC,
    POPULARITY_DESC,
    TRENDING_DESC,
    FAVOURITES_DESC,
}
