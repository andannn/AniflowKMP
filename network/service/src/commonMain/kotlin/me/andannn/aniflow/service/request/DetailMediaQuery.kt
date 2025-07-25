package me.andannn.aniflow.service.request

import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.network.common.schemas.MEDIA_DETAIL_QUERY_SCHEMA

internal data class DetailMediaQuery(
    val id: Int,
) : GraphQLQuery<DataWrapper<MediaDetailResponse>> {
    override val schema: GraphQLSchema = MediaDetailSchema
    override val variables: List<Any>
        get() =
            listOf(
                component1(),
            )
}

private val MediaDetailSchema =
    GraphQLSchema(
        schema = MEDIA_DETAIL_QUERY_SCHEMA,
        params =
            listOf(
                Param("id", Int::class),
            ),
    )
