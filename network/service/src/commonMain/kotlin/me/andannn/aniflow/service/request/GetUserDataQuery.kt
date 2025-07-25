package me.andannn.aniflow.service.request

import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.User
import me.andannn.network.common.schemas.USER_DATA_MUTATION_SCHEMA

internal data object GetUserDataQuery : GraphQLQuery<DataWrapper<User>> {
    override val schema: GraphQLSchema = MediaDetailSchema
    override val variables: List<Any>
        get() = listOf()
}

private val MediaDetailSchema =
    GraphQLSchema(
        schema = USER_DATA_MUTATION_SCHEMA,
        params = listOf(),
    )
