/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.ActivityUnion
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.enums.ActivityType
import me.andannn.network.common.schemas.ACTIVITY_PAGE_QUERY_SCHEMA

@Serializable
internal data class ActivityPageScheduleQuery(
    val page: Int,
    val perPage: Int,
    @SerialName("type_in")
    val isFollowing: Boolean? = null,
    val typeIn: List<ActivityType> = emptyList(),
    val userId: Int? = null,
    val mediaId: Int? = null,
    val hasRepliesOrTypeText: Boolean? = null,
) : GraphQLQuery<DataWrapper<PageWrapper<ActivityUnion>>> {
    override fun getSchema() = ACTIVITY_PAGE_QUERY_SCHEMA
}
