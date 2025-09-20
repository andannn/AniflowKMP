/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class StaffConnection(
    /**
     * The pagination information
     */
    public val pageInfo: PageInfo? = null,
    public val edges: List<Edge?>? = null,
) {
    @Serializable
    public data class Edge(
        /**
         * The role of the staff member in the production of the media
         */
        public val role: String? = null,
        public val node: Staff? = null,
    )
}

fun StaffConnection.toPage() =
    Page(
        pageInfo = pageInfo,
        items = edges?.filterNotNull() ?: emptyList(),
    )
