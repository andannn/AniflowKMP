/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializerOrNull
import me.andannn.network.common.GraphQLBody

internal interface GraphQLQuery<T> {
    fun getSchema(): String
}

internal inline fun <reified T : GraphQLQuery<*>> T.toQueryBody(): GraphQLBody {
    val typeInfo = typeInfo<T>()
    val serializer =
        typeInfo.kotlinType
            ?.let {
                Json.serializersModule.serializerOrNull(it)
            }
            ?: throw IllegalArgumentException("No serializer found for type: ${typeInfo.type}")
    return GraphQLBody(
        query = getSchema(),
        variables = Json.encodeToString(serializer, this),
    )
}
