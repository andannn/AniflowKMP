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
import me.andannn.aniflow.service.dto.enums.MediaFormat
import me.andannn.aniflow.service.dto.enums.MediaSeason
import me.andannn.aniflow.service.dto.enums.MediaSort
import me.andannn.aniflow.service.dto.enums.MediaStatus
import me.andannn.aniflow.service.dto.enums.MediaType
import me.andannn.network.common.schemas.MEDIA_PAGE_QUERY_SCHEMA

@Serializable
internal data class MediaPageQuery(
    val page: Int,
    val perPage: Int,
    val type: MediaType?,
    val countryCode: String?,
    val seasonYear: Int?,
    val season: MediaSeason?,
    val status: MediaStatus?,
    val sort: List<MediaSort>?,
    @SerialName("format_in")
    val formatIn: List<MediaFormat>?,
    val isAdult: Boolean?,
    val startDateGreater: String?,
    val endDateLesser: String?,
) : GraphQLQuery<DataWrapper<PageWrapper<Media>>> {
    override fun getSchema() = MEDIA_PAGE_QUERY_SCHEMA
}
