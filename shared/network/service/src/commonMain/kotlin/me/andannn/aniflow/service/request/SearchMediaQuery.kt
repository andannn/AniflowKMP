/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.enums.MediaType
import me.andannn.network.common.schemas.SEARCH_MEDIA_QUERY_SCHEMA

@Serializable
internal data class SearchMediaQuery(
    val page: Int,
    val perPage: Int,
    @SerialName("search")
    val keyword: String,
    val type: MediaType,
    val isAdult: Boolean,
) : GraphQLQuery<DataWrapper<PageWrapper<Media>>> {
    override fun getSchema() = SEARCH_MEDIA_QUERY_SCHEMA
}
