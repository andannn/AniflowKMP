/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.CharacterDetailResponse
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.network.common.schemas.CHARACTER_DETAIL_QUERY_SCHEMA

@Serializable
internal data class CharacterDetailQuery(
    val id: Int,
    @SerialName("page")
    val mediaConnectionPage: Int?,
    @SerialName("perPage")
    val mediaConnectionPerPage: Int?,
) : GraphQLQuery<DataWrapper<CharacterDetailResponse>> {
    override fun getSchema() = CHARACTER_DETAIL_QUERY_SCHEMA
}
