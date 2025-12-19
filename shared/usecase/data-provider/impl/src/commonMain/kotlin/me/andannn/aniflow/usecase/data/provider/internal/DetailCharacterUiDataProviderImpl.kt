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
import me.andannn.aniflow.usecase.data.provider.DetailCharacterUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DetailCharacterUiState
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncDetailCharacterTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.createSideEffectFlow

internal class DetailCharacterUiDataProviderImpl(
    override val characterId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : DetailCharacterUiDataProvider {
    override fun uiDataFlow(): Flow<DetailCharacterUiState> =
        combine(
            mediaRepository.getDetailCharacter(characterId),
            authRepository.getUserOptionsFlow(),
        ) { character, userOptions ->
            DetailCharacterUiState(
                characterModel = character,
                userOption = userOptions,
            )
        }.distinctUntilChanged()

    override fun uiSideEffect(forceRefreshFirstTime: Boolean) =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncDetailCharacterTask(characterId),
        )
}
