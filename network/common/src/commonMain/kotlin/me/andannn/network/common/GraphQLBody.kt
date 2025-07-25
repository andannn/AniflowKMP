package me.andannn.network.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class GraphQLBody(
    val query: String,
    val variables: Map<String, JsonPrimitive>
)
