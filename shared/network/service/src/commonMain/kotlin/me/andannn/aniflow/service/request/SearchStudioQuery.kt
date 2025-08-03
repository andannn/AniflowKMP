/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.Studio
import me.andannn.network.common.schemas.SEARCH_STUDIO_QUERY_SCHEMA

@Serializable
internal data class SearchStudioQuery(
    val page: Int,
    val perPage: Int,
    val keyword: String,
) : GraphQLQuery<DataWrapper<PageWrapper<Studio>>> {
    override fun getSchema() = SEARCH_STUDIO_QUERY_SCHEMA
}
