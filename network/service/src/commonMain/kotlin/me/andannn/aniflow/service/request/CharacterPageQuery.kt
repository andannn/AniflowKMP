/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.dto.enums.StaffLanguage
import me.andannn.network.common.schemas.CHARACTER_PAGE_QUERY_SCHEMA
import kotlin.jvm.Transient

@Serializable
internal data class CharacterPageQuery(
    val page: Int,
    val perPage: Int,
    val mediaId: Int,
    val staffLanguage: StaffLanguage,
) : GraphQLQuery<DataWrapper<MediaDetailResponse>> {
    @Transient
    override val schema: String = CHARACTER_PAGE_QUERY_SCHEMA
}
