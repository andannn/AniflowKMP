/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.toMediaType
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.usecase.data.provider.DiscoverUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DiscoverUiState
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import me.andannn.aniflow.usecase.data.provider.internal.tasks.RefreshAllCategoriesTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncUserConditionTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncUserMediaListTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.createSideEffectFlow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class DiscoverUiDataProviderImpl(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) : DiscoverUiDataProvider {
    override fun uiDataFlow(): Flow<DiscoverUiState> =
        context(mediaRepo, authRepo) {
            discoverUiStateFlow()
        }

    override fun uiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus> =
        createSideEffectFlow(
            forceRefreshFirstTime,
            RefreshAllCategoriesTask(),
            SyncUserMediaListTask(),
            SyncUserConditionTask(),
        )
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun discoverUiStateFlow(): Flow<DiscoverUiState> {
    val authedUserFlow = authRepo.getAuthedUserFlow()
    val userOptionFlow = authRepo.getUserOptionsFlow()
    val contentModeFlow = mediaRepo.getContentModeFlow()
    val categoryDataFlow =
        contentModeFlow.flatMapLatest { mode ->
            val allCategories = mode.toMediaType().allCategories()
            val dataFlowList =
                allCategories.map { category ->
                    mediaRepo.getMediasFlow(category)
                }

            combine(
                dataFlowList,
            ) {
                CategoryDataModel(it.toList())
            }
        }

    val newReleasedFlow =
        combine(
            authedUserFlow,
            contentModeFlow,
        ) { authedUser, contentMode ->
            Pair(authedUser, contentMode)
        }.distinctUntilChanged()
            .flatMapLatest { (authUser, contentMode) ->
                if (authUser != null && contentMode == MediaContentMode.ANIME) {
                    mediaRepo
                        .getNewReleasedAnimeListFlow(
                            userId = authUser.id,
                            timeSecondLaterThan =
                                Clock.System
                                    .now()
                                    .minus(MediaWithMediaListItem.NEW_RELEASED_DAYS_THRESHOLD.days)
                                    .epochSeconds,
                        ).map {
                            it
                                .filter(MediaWithMediaListItem::haveNextEpisode)
                                .sortedByDescending(MediaWithMediaListItem::airingScheduleUpdateTime)
                        }
                } else {
                    flow { emit(emptyList()) }
                }
            }.onStart { emit(emptyList()) }

    return combine(
        categoryDataFlow,
        newReleasedFlow,
        userOptionFlow,
    ) { categoryData, newReleasedMedia, userOption ->
        DiscoverUiState(
            categoryDataMap = categoryData,
            newReleasedMedia = newReleasedMedia,
            userOptions = userOption,
        )
    }.distinctUntilChanged()
}
