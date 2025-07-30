/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaListResponse
import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.network.common.schemas.MEDIA_LIST_QUERY_SCHEMA

@Serializable
internal data class MediaListQuery(
    val mediaId: Int,
    val userId: Int,
    val scoreFormat: ScoreFormat,
) : GraphQLQuery<DataWrapper<MediaListResponse>> {
    override fun getSchema() = MEDIA_LIST_QUERY_SCHEMA
}
