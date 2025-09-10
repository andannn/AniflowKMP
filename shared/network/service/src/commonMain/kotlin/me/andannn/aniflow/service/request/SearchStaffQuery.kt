/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.Staff
import me.andannn.network.common.schemas.SEARCH_STAFF_QUERY_SCHEMA

@Serializable
internal data class SearchStaffQuery(
    val page: Int,
    val perPage: Int,
    @SerialName("search")
    val keyword: String?,
) : GraphQLQuery<DataWrapper<PageWrapper<Staff>>> {
    override fun getSchema() = SEARCH_STAFF_QUERY_SCHEMA
}
