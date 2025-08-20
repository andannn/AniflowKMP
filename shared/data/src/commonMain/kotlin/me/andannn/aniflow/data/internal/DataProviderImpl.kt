/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DiscoverUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.TrackUiDataProvider
import me.andannn.aniflow.data.internal.tasks.RefreshAllCategoriesTask
import me.andannn.aniflow.data.internal.tasks.SyncUserMediaListTask
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.DiscoverUiState
import me.andannn.aniflow.data.model.TrackUiState
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.toMediaType
import me.andannn.aniflow.data.model.relation.CategoryDataModel

internal class DataProviderImpl(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) : DiscoverUiDataProvider, TrackUiDataProvider {
    @NativeCoroutines
    override fun discoverUiDataFlow(): Flow<DiscoverUiState> =
        with(mediaRepo) {
            with(authRepo) {
                return discoverUiStateFlow()
            }
        }

    @NativeCoroutines
    override fun discoverUiSideEffect(): Flow<SyncStatus> = createSideEffectFlow(
        RefreshAllCategoriesTask(),
        SyncUserMediaListTask()
    )

    @NativeCoroutines
    override fun trackUiDataFlow(): Flow<TrackUiState> = with(mediaRepo) {
        with(authRepo) {
            return trackUiStateFlow()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutines
    override fun trackUiSideEffect(): Flow<SyncStatus> = createSideEffectFlow(
        SyncUserMediaListTask()
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun discoverUiStateFlow(): Flow<DiscoverUiState> {
    val categoryDataFlow =
        mediaRepo.getContentModeFlow().flatMapLatest { mode ->
            val allCategories = mode.toMediaType().allCategories()
            val dataFlowList = allCategories.map { category ->
                mediaRepo.getMediasFlow(category)
            }

            combine(
                dataFlowList,
            ) {
                CategoryDataModel(it.toList())
            }
        }

    val authedUserFlow = authRepo.getAuthedUser()
    val contentModeFlow = mediaRepo.getContentModeFlow()
    return combine(
        categoryDataFlow,
        authedUserFlow,
        contentModeFlow,
    ) { categoryData, authedUser, contentMode ->
        DiscoverUiState(
            categoryDataMap = categoryData,
            authedUser = authedUser,
            contentMode = contentMode,
        )
    }.distinctUntilChanged()
}

@OptIn(ExperimentalCoroutinesApi::class)
context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun trackUiStateFlow(): Flow<TrackUiState> {
    val userWithContentModeFlow = combine(
        authRepo.getAuthedUser(),
        mediaRepo.getContentModeFlow(),
    ) { authedUser, contentMode -> Pair(authedUser, contentMode) }

    val trackUiFlow = userWithContentModeFlow
        .distinctUntilChanged()
        .flatMapLatest { (authUser, contentMode) ->
            if (authUser == null) {
                // If not authenticated, return an empty flow
                emptyFlow()
            } else {
                mediaRepo.getMediaListFlowByUserId(
                    userId = authUser.id,
                    mediaListStatus =
                        listOf(
                            MediaListStatus.PLANNING,
                            MediaListStatus.CURRENT,
                        ),
                    mediaType = contentMode.toMediaType(),
                )
            }
        }
    return trackUiFlow.map {
        TrackUiState(
            items = it,
        )
    }
}
