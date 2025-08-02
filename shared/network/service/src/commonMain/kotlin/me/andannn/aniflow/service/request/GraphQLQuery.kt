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

private val CustomJson =
    Json {
        explicitNulls = false
    }

internal inline fun <reified T : GraphQLQuery<*>> T.toQueryBody(): GraphQLBody {
    val typeInfo = typeInfo<T>()
    val serializer =
        typeInfo.kotlinType
            ?.let {
                CustomJson.serializersModule.serializerOrNull(it)
            }
            ?: throw IllegalArgumentException("No serializer found for type: ${typeInfo.type}")
    return GraphQLBody(
        query = getSchema(),
        variables = CustomJson.encodeToString(serializer, this),
    )
}
