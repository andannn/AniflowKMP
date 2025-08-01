/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.AiringSchedule
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.network.common.schemas.AIRING_SCHEDULE_QUERY_SCHEMA

@Serializable
internal data class AiringScheduleQuery(
    val page: Int,
    val perPage: Int,
    @SerialName("airingAt_greater")
    val airingAtGreater: Int,
    @SerialName("airingAt_lesser")
    val airingAtLesser: Int,
) : GraphQLQuery<DataWrapper<PageWrapper<AiringSchedule>>> {
    override fun getSchema() = AIRING_SCHEDULE_QUERY_SCHEMA
}
