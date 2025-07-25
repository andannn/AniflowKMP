package me.andannn.aniflow.service.request

import kotlinx.serialization.json.JsonPrimitive
import me.andannn.network.common.GraphQLBody
import kotlin.reflect.KClass

internal data class Param(
    val name: String,
    val type: KClass<*>,
)

internal data class GraphQLSchema(
    val schema: String,
    val params: List<Param>,
)

internal interface GraphQLQuery<T> {
    val schema: GraphQLSchema

    val variables: List<Any>
}

internal fun GraphQLQuery<*>.toQueryBody(): GraphQLBody =
    GraphQLBody(
        query = schema.schema,
        variables =
            schema.params
                .mapIndexed { index, param ->
                    val value = variables[index]
                    param.name to
                        when (value) {
                            is String -> JsonPrimitive(value)
                            is Number -> JsonPrimitive(value)
                            else -> throw IllegalArgumentException("Unsupported variable type: ${value::class}")
                        }
                }.toMap(),
    )
