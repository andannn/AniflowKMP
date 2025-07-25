package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class StaffConnection(
    /**
     * The pagination information
     */
    public val pageInfo: PageInfo?,
    public val edges: List<Edge?>?,
) {

    @Serializable
    public data class Edge(
        /**
         * The role of the staff member in the production of the media
         */
        public val role: String?,
        public val node: Staff?,
    )
}
