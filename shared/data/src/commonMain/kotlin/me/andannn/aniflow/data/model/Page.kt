/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

data class PageInfo(
    public val total: Int,
    /**
     * The count on a page
     */
    public val perPage: Int,
    /**
     * The current page
     */
    public val currentPage: Int,
    /**
     * The last page
     */
    public val lastPage: Int,
    /**
     * If there is another page
     */
    public val hasNextPage: Boolean,
)

data class Page<T>(
    /**
     * The page info
     */
    val pageInfo: PageInfo,
    /**
     * The items on the page
     */
    val items: List<T>,
)
