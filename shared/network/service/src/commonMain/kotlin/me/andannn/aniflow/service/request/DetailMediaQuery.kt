/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.dto.enums.StaffLanguage
import me.andannn.network.common.schemas.buildMediaDetailQuerySchema

@Serializable
internal data class DetailMediaQuery(
    val id: Int,
    val characterPage: Int? = null,
    val characterPerPage: Int? = null,
    @SerialName("staffLanguage")
    val characterStaffLanguage: StaffLanguage? = null,
    val staffPage: Int? = null,
    val staffPerPage: Int? = null,
    @Transient
    val withStudioConnection: Boolean = false,
    @Transient
    val withRelationConnection: Boolean = false,
) : GraphQLQuery<DataWrapper<MediaDetailResponse>> {
    override fun getSchema() =
        buildMediaDetailQuerySchema(
            withCharacterConnection = characterPage != null || characterPerPage != null || characterStaffLanguage != null,
            withStaffConnection = staffPage != null || staffPerPage != null,
            withStudioConnection = withStudioConnection,
            withRelations = withRelationConnection,
        )
}
