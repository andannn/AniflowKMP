/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.paging

data class PageConfig(
    val perPage: Int,
)

internal val DEFAULT_CONFIG =
    PageConfig(
        perPage = 20,
    )
