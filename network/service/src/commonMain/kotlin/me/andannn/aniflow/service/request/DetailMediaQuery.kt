package me.andannn.aniflow.service.request

import me.andannn.aniflow.service.GraphQLQuery
import me.andannn.aniflow.service.GraphQLSchema
import me.andannn.aniflow.service.Param
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.network.common.MediaDetailQuerySchema

internal data class DetailMediaQuery(
    val id: Int,
) : GraphQLQuery<DataWrapper<MediaDetailResponse>> {
    override val schema: GraphQLSchema = MediaDetailSchema
    override val variables: List<Any>
        get() = listOf(
            component1()
        )
}

private val MediaDetailSchema = GraphQLSchema(
    schema = MediaDetailQuerySchema,
    params = listOf(
        Param("id", Int::class)
    )
)
