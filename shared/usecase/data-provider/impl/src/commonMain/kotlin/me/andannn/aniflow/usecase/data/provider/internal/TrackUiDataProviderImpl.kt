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
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.toMediaType
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import me.andannn.aniflow.usecase.data.provider.TrackUiDataProvider
import me.andannn.aniflow.usecase.data.provider.TrackUiState
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncUserMediaListTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.createSideEffectFlow

class TrackUiDataProviderImpl(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) : TrackUiDataProvider {
    override fun uiDataFlow(): Flow<TrackUiState> =
        context(mediaRepo, authRepo) {
            trackUiStateFlow()
        }

    override fun uiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus> =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncUserMediaListTask(),
        )
}

@OptIn(ExperimentalCoroutinesApi::class)
context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun trackUiStateFlow(): Flow<TrackUiState> {
    val authedUserFlow = authRepo.getAuthedUserFlow()
    val userWithContentModeFlow =
        combine(
            authedUserFlow,
            mediaRepo.getContentModeFlow(),
        ) { authedUser, contentMode -> Pair(authedUser, contentMode) }
    val userOptionsFlow = authRepo.getUserOptionsFlow()

    val mediaListItemsFlow =
        userWithContentModeFlow
            .distinctUntilChanged()
            .flatMapLatest { (authUser, contentMode) ->
                if (authUser == null) {
                    // If not authenticated, emit an empty list
                    flow { emit(emptyList()) }
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
    return combine(
        mediaListItemsFlow,
        authedUserFlow,
        userOptionsFlow,
    ) { mediaListItems, authedUser, userOptions ->
        TrackUiState(items = mediaListItems, userOptions = userOptions, authedUser = authedUser)
    }.distinctUntilChanged()
}
