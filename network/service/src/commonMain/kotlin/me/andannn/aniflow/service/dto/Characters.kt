/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.CharacterRole

@Serializable
public data class Characters(
    /**
     * The pagination information
     */
    public val pageInfo: PageInfo? = null,
    public val edges: List<Edge?>? = null,
) {
    @Serializable
    public data class Edge(
        /**
         * The characters role in the media
         */
        public val role: CharacterRole? = null,
        public val node: Character? = null,
        /**
         * The voice actors of the character
         */
        public val voiceActors: List<Staff?>? = null,
    )
}
