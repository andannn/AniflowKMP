/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.network.common.schemas.MEDIA_DETAIL_QUERY_SCHEMA

@Serializable
internal data class DetailMediaQuery(
    val id: Int,
) : GraphQLQuery<DataWrapper<MediaDetailResponse>> {
    @Transient
    override val schema: String = MEDIA_DETAIL_QUERY_SCHEMA
}
