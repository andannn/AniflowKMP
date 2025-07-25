package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.CharacterRole

@Serializable
public data class Characters(
    /**
     * The pagination information
     */
    public val pageInfo: PageInfo?,
    public val edges: List<Edge?>?,
) {
    @Serializable
    public data class Edge(
        /**
         * The characters role in the media
         */
        public val role: CharacterRole?,
        public val node: Character?,
        /**
         * The voice actors of the character
         */
        public val voiceActors: List<Staff?>?,
    )
}
