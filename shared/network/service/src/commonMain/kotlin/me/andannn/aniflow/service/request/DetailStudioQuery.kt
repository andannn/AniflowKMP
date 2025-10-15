/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.StudioDetailResponse
import me.andannn.aniflow.service.dto.enums.MediaSort
import me.andannn.network.common.schemas.STUDIO_DETAIL_QUERY_SCHEMA

@Serializable
internal data class DetailStudioQuery(
    @SerialName("id")
    val studioId: Int,
    @SerialName("page")
    val mediaConnectionPage: Int?,
    @SerialName("perPage")
    val mediaConnectionPerPage: Int?,
    @SerialName("sort")
    val mediaSort: List<MediaSort> = emptyList(),
) : GraphQLQuery<DataWrapper<StudioDetailResponse>> {
    override fun getSchema() = STUDIO_DETAIL_QUERY_SCHEMA
}
