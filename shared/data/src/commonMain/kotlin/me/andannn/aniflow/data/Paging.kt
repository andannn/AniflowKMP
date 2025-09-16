/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.andannn.aniflow.data.internal.paging.DEFAULT_CONFIG
import me.andannn.aniflow.data.internal.paging.DefaultPageComponent
import me.andannn.aniflow.data.internal.paging.PageConfig
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.SearchSource
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.NotificationCategory
import org.koin.mp.KoinPlatform.getKoin

// interop with swift.
object PageComponentFactory {
    fun createMediaCategoryPageComponent(category: MediaCategory): MediaCategoryPageComponent = MediaCategoryPageComponent(category)

    fun createNotificationPageComponent(category: NotificationCategory): NotificationPageComponent = NotificationPageComponent(category)
}

sealed interface LoadingStatus {
    data object Idle : LoadingStatus

    data object AllLoaded : LoadingStatus

    data object Loading : LoadingStatus

    data class Error(
        val error: AppError,
    ) : LoadingStatus
}

interface PageComponent<T> {
    @NativeCoroutines
    val items: StateFlow<List<T>>

    @NativeCoroutines
    val status: StateFlow<LoadingStatus>

    fun loadNextPage()

    /**
     * Disposes the component and cancels any ongoing operations.
     * This should be called when the component is no longer needed
     */
    fun dispose()
}

object EmptyPageComponent : PageComponent<Nothing> {
    override val items: StateFlow<List<Nothing>> = MutableStateFlow(emptyList())
    override val status: StateFlow<LoadingStatus> = MutableStateFlow(LoadingStatus.Idle)

    override fun loadNextPage() {}

    override fun dispose() {}
}

class MediaCategoryPageComponent(
    category: MediaCategory,
    config: PageConfig = DEFAULT_CONFIG,
    private val mediaRepository: MediaRepository = getKoin().get(),
    errorHandler: AppErrorHandler? = null,
) : PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
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
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<NotificationModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
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

class MediaSearchResultPageComponent(
    config: PageConfig = DEFAULT_CONFIG,
    private val source: SearchSource.Media,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository
                .searchMediaFromSource(
                    page = page,
                    perPage = perPage,
                    searchSource = source,
                )
        },
    )

class CharacterSearchResultPageComponent(
    config: PageConfig = DEFAULT_CONFIG,
    private val source: SearchSource.Character,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<CharacterModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository
                .searchCharacterFromSource(
                    page = page,
                    perPage = perPage,
                    searchSource = source,
                )
        },
    )

class StaffSearchResultPageComponent(
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val source: SearchSource.Staff,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<StaffModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository
                .searchStaffFromSource(
                    page = page,
                    perPage = perPage,
                    searchSource = source,
                )
        },
    )

class StudioSearchResultPageComponent(
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val source: SearchSource.Studio,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<StudioModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository
                .searchStudioFromSource(
                    page = page,
                    perPage = perPage,
                    searchSource = source,
                )
        },
    )
