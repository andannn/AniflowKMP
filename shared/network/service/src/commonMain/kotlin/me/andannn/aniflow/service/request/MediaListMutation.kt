/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.FuzzyDate
import me.andannn.aniflow.service.dto.SavedMediaListResponse
import me.andannn.aniflow.service.dto.enums.MediaListStatus
import me.andannn.network.common.schemas.MEDIA_LIST_MUTATION_SCHEMA

@Serializable
internal data class MediaListMutation(
    @SerialName("id")
    val mediaListId: Int?,
    val mediaId: Int?,
    val progress: Int?,
    val status: MediaListStatus?,
    val score: Float?,
    val progressVolumes: Int?,
    val repeat: Int?,
    val private: Boolean?,
    val notes: String?,
    val startedAt: FuzzyDate?,
    val completedAt: FuzzyDate?,
) : GraphQLQuery<DataWrapper<SavedMediaListResponse>> {
    override fun getSchema() = MEDIA_LIST_MUTATION_SCHEMA
}
