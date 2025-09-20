/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DetailCharacterUiDataProvider
import me.andannn.aniflow.data.DetailStaffUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.internal.tasks.SyncDetailCharacterTask
import me.andannn.aniflow.data.internal.tasks.SyncDetailStaffTask
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.DetailCharacterUiState
import me.andannn.aniflow.data.model.DetailStaffUiState

internal class DetailCharacterUiDataProviderImpl(
    override val characterId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : DetailCharacterUiDataProvider {
    override fun detailUiDataFlow(): Flow<DetailCharacterUiState> =
        combine(
            mediaRepository.getDetailCharacter(characterId),
            authRepository.getUserOptionsFlow(),
        ) { character, userOptions ->
            DetailCharacterUiState(
                characterModel = character,
                userOption = userOptions,
            )
        }.distinctUntilChanged()

    override fun detailUiSideEffect(forceRefreshFirstTime: Boolean) =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncDetailCharacterTask(characterId),
        )
}
