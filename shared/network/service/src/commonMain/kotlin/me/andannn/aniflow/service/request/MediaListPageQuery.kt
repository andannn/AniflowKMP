/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.enums.MediaListStatus
import me.andannn.aniflow.service.dto.enums.MediaType
import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.network.common.schemas.MEDIA_LIST_PAGE_QUERY_SCHEMA

@Serializable
internal data class MediaListPageQuery(
    val page: Int,
    val perPage: Int,
    val userId: Int,
    val statusIn: List<MediaListStatus>,
    val type: MediaType,
    val format: ScoreFormat,
) : GraphQLQuery<DataWrapper<PageWrapper<MediaList>>> {
    override fun getSchema() = MEDIA_LIST_PAGE_QUERY_SCHEMA
}
