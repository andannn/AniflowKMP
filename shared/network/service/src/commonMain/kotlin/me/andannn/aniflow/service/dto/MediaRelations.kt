/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.MediaRelation

@Serializable
public data class MediaRelations(
    public val edges: List<Edge?>?,
) {
    @Serializable
    public data class Edge(
        /**
         * The type of relation to the parent model
         */
        public val relationType: MediaRelation?,
        public val node: Media?,
    )
}
