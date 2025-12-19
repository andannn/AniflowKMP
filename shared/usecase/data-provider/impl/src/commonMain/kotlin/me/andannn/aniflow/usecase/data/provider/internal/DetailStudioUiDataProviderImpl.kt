/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.usecase.data.provider.DetailStudioState
import me.andannn.aniflow.usecase.data.provider.DetailStudioUiDataProvider
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncDetailStudioTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.createSideEffectFlow

class DetailStudioUiDataProviderImpl(
    override val studioId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : DetailStudioUiDataProvider {
    override fun uiDataFlow(): Flow<DetailStudioState> =
        combine(
            mediaRepository.getDetailStudio(studioId),
            authRepository.getUserOptionsFlow(),
        ) { studio, userOptions ->
            DetailStudioState(
                studioModel = studio,
                userOption = userOptions,
            )
        }.distinctUntilChanged()

    override fun uiSideEffect(forceRefreshFirstTime: Boolean) =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncDetailStudioTask(studioId),
        )
}
