package me.andannn.aniflow.service

import kotlinx.serialization.Serializable
import me.andannn.network.common.GraphQLBody
import kotlin.reflect.KClass

internal data class Param(
    val name: String,
    val type: KClass<*>
)

internal data class GraphQLSchema(
    val schema: String,
    val params: List<Param>
)

internal interface GraphQLQuery<T> {
    val schema: GraphQLSchema

    val variables: List<Any>
}

internal fun GraphQLQuery<*>.toQueryBody(): GraphQLBody {
    return GraphQLBody(
        query = schema.schema,
        variables = schema.params.mapIndexed { index, param ->
            val value = variables[index]
            param.name to when (value) {
                is String -> value
                is Number -> value.toString()
                else -> throw IllegalArgumentException("Unsupported variable type: ${value::class}")
            }
        }.toMap()
    )
}