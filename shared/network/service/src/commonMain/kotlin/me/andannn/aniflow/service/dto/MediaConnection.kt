/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.CharacterRole
import me.andannn.aniflow.service.dto.enums.MediaRelation

@Serializable
public data class MediaConnection(
    /**
     * The pagination information
     */
    public val pageInfo: PageInfo? = null,
    public val edges: List<Edge>,
) {
    @Serializable
    public data class Edge(
        /**
         * The role of the staff member in the production of the media
         */
        public val relationType: MediaRelation? = null,
        /**
         * The characters in the media voiced by the parent actor
         */
        public val characters: List<Character?>? = null,
        /**
         * The characters role in the media
         */
        public val characterRole: CharacterRole? = null,
        public val node: Media? = null,
    )
}
