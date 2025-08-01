/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.aniflow.service.dto.NotificationUnion
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.enums.NotificationType
import me.andannn.network.common.schemas.NOTIFICATION_PAGE_QUERY_SCHEMA

@Serializable
internal data class NotificationQuery(
    val page: Int,
    val perPage: Int,
    @SerialName("type_in")
    val notificationTypeIn: List<NotificationType>,
    val resetNotificationCount: Boolean,
) : GraphQLQuery<DataWrapper<PageWrapper<NotificationUnion>>> {
    override fun getSchema() = NOTIFICATION_PAGE_QUERY_SCHEMA
}
