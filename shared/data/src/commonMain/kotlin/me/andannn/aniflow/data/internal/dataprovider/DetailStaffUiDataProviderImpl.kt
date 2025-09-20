/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DetailStaffUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.internal.tasks.SyncDetailStaffTask
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.DetailStaffUiState

class DetailStaffUiDataProviderImpl(
    override val staffId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : DetailStaffUiDataProvider {
    override fun detailUiDataFlow(): Flow<DetailStaffUiState> =
        combine(
            mediaRepository.getDetailStaff(staffId),
            authRepository.getUserOptionsFlow(),
        ) { staff, userOptions ->
            DetailStaffUiState(
                staffModel = staff,
                userOption = userOptions,
            )
        }.distinctUntilChanged()

    override fun detailUiSideEffect(forceRefreshFirstTime: Boolean) =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncDetailStaffTask(staffId),
        )
}
