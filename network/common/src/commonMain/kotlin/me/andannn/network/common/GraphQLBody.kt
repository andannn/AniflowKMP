package me.andannn.network.common

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLBody(
    val query: String,
    val variables: String,
)
