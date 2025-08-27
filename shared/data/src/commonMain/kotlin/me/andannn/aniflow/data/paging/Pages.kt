/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.paging

import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.NotificationCategory
import org.koin.mp.KoinPlatform.getKoin

object PageComponentFactory {
    fun createMediaCategoryPageComponent(category: MediaCategory): MediaCategoryPageComponent = MediaCategoryPageComponent(category)

    fun createNotificationPageComponent(category: NotificationCategory): NotificationPageComponent = NotificationPageComponent(category)
}

class MediaCategoryPageComponent(
    category: MediaCategory,
    config: PageConfig = DEFAULT_CONFIG,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        onLoadPage = { page, perPage ->
            mediaRepository
                .loadMediaPageByCategory(
                    category = category,
                    page = page,
                    perPage = perPage,
                )
        },
    )

class NotificationPageComponent(
    notificationCategory: NotificationCategory,
    config: PageConfig = DEFAULT_CONFIG,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<NotificationModel> by DefaultPageComponent(
        config = config,
        onLoadPage = { page, perPage ->
            mediaRepository
                .loadNotificationByPage(
                    category = notificationCategory,
                    page = page,
                    perPage = perPage,
                    resetNotificationCount = true,
                )
        },
    )
