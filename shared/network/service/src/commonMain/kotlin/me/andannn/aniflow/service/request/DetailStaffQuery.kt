/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.StaffDetailResponse
import me.andannn.aniflow.service.dto.enums.MediaSort
import me.andannn.network.common.schemas.STAFF_DETAIL_QUERY_SCHEMA

@Serializable
internal data class DetailStaffQuery(
    @SerialName("id")
    val staffId: Int,
    @SerialName("page")
    val characterConnectionPage: Int?,
    @SerialName("perPage")
    val characterConnectionPerPage: Int?,
    @SerialName("sort")
    val mediaSort: List<MediaSort> = emptyList(),
) : GraphQLQuery<DataWrapper<StaffDetailResponse>> {
    override fun getSchema() = STAFF_DETAIL_QUERY_SCHEMA
}
