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
import me.andannn.aniflow.data.model.StaffWithRole
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor
import me.andannn.aniflow.data.model.relation.VoicedCharacterWithMedia
import org.koin.mp.KoinPlatform.getKoin

// interop with swift.
object PageComponentFactory {
    fun createMediaCategoryPageComponent(
        category: MediaCategory,
        errorHandler: AppErrorHandler,
    ): MediaCategoryPageComponent = MediaCategoryPageComponent(category, errorHandler = errorHandler)

    fun createNotificationPageComponent(
        category: NotificationCategory,
        errorHandler: AppErrorHandler,
    ): NotificationPageComponent = NotificationPageComponent(category, errorHandler = errorHandler)

    fun createDetailMediaStaffPaging(mediaId: String): DetailMediaStaffPageComponent = DetailMediaStaffPageComponent(mediaId)

    fun createDetailMediaCharacterPaging(mediaId: String): DetailMediaCharacterPageComponent =
        DetailMediaCharacterPageComponent(
            mediaId,
            StaffLanguage.JAPANESE,
        )

    fun createStaffCharactersPaging(
        staffId: String,
        sort: MediaSort,
    ): StaffCharactersPageComponent =
        StaffCharactersPageComponent(
            staffId,
            sort,
        )

    fun characterDetailMediaPaging(
        characterId: String,
        sort: MediaSort,
    ): CharacterDetailMediaPaging =
        CharacterDetailMediaPaging(
            characterId,
            sort,
        )
}

sealed interface LoadingStatus {
    data object Idle : LoadingStatus

    data object AllLoaded : LoadingStatus

    data object Loading : LoadingStatus

    data class Error(
        val error: AppError,
    ) : LoadingStatus
}

interface PageComponent<out T : Any> {
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

    companion object {
        fun <T : Any> empty() = EmptyPageComponent<T>()
    }
}

class EmptyPageComponent<T : Any> : PageComponent<T> {
    override val items: StateFlow<List<T>> = MutableStateFlow(emptyList())
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

class DetailMediaStaffPageComponent(
    private val mediaId: String,
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<StaffWithRole> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository
                .getStaffPageOfMedia(
                    mediaId = mediaId,
                    page = page,
                    perPage = perPage,
                )
        },
    )

class DetailMediaCharacterPageComponent(
    private val mediaId: String,
    private val characterStaffLanguage: StaffLanguage,
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<CharacterWithVoiceActor> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository
                .getCharacterPageOfMedia(
                    mediaId = mediaId,
                    characterStaffLanguage = characterStaffLanguage,
                    page = page,
                    perPage = perPage,
                )
        },
    )

class StaffCharactersPageComponent(
    private val staffId: String,
    private val sort: MediaSort,
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<VoicedCharacterWithMedia> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository.getMediaPageOfStaff(
                staffId = staffId,
                page = page,
                perPage = perPage,
                mediaSort = sort,
            )
        },
    )

class CharacterDetailMediaPaging(
    private val characterId: String,
    private val sort: MediaSort,
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository.getMediaPageOfCharacter(
                character = characterId,
                page = page,
                perPage = perPage,
                mediaSort = sort,
            )
        },
    )

class StudioMediaConnectionPageComponent(
    private val studioId: String,
    private val sort: MediaSort,
    config: PageConfig = DEFAULT_CONFIG,
    private val errorHandler: AppErrorHandler? = null,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        errorHandler = errorHandler,
        onLoadPage = { page, perPage ->
            mediaRepository.getMediaPageOfStudio(
                studioId = studioId,
                page = page,
                perPage = perPage,
                mediaSort = sort,
            )
        },
    )
