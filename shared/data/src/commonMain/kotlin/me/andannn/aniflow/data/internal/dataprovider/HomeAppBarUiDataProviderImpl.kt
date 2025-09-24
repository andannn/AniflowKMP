/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.HomeAppBarUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.HomeAppBarUiState

class HomeAppBarUiDataProviderImpl(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) : HomeAppBarUiDataProvider {
    override fun appBarFlow(): Flow<HomeAppBarUiState> =
        flow {
            val authUserFlow = authRepo.getAuthedUserFlow()
            val contentModeFlow = mediaRepo.getContentModeFlow()

            combine(
                authUserFlow,
                contentModeFlow,
            ) { authedUser, contentMode ->
                HomeAppBarUiState(
                    authedUser = authedUser,
                    contentMode = contentMode,
                )
            }.distinctUntilChanged().collect {
                emit(it)
            }
        }
}
