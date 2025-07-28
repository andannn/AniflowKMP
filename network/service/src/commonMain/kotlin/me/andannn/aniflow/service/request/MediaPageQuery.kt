/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

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
import kotlin.jvm.Transient

@Serializable
internal data class MediaPageQuery(
    val page: Int,
    val perPage: Int,
    val type: MediaType? = null,
    val countryCode: String? = null,
    val seasonYear: Int? = null,
    val season: MediaSeason? = null,
    val status: MediaStatus? = null,
    val sort: List<MediaSort>? = null,
    val formatIn: List<MediaFormat>? = null,
    val isAdult: Boolean? = null,
    val startDateGreater: String? = null,
    val endDateLesser: String? = null,
) : GraphQLQuery<DataWrapper<PageWrapper<Media>>> {
    @Transient
    override val schema: String = MEDIA_PAGE_QUERY_SCHEMA
}
