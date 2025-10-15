/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.DetailCharacterUiState
import me.andannn.aniflow.data.model.DetailStaffUiState
import me.andannn.aniflow.data.model.DetailStudioState
import me.andannn.aniflow.data.model.DetailUiState
import me.andannn.aniflow.data.model.DiscoverUiState
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.SettingUiState
import me.andannn.aniflow.data.model.TrackProgressDialogState
import me.andannn.aniflow.data.model.TrackUiState

/**
 * Represents the status of a sync operation.
 * It can be in a loading state, or idle with a list of errors.
 */
sealed class SyncStatus {
    /**
     * Represents a loading state for a sync operation.
     */
    data object Loading : SyncStatus()

    /**
     * Represents an idle state for a sync operation, which may include errors.
     *
     * @property errors A list of errors that occurred during the sync operation.
     */
    data class Idle(
        val errors: List<AppError> = emptyList(),
    ) : SyncStatus()

    fun isLoading(): Boolean = this is Loading
}

interface HomeAppBarUiDataProvider {
    @NativeCoroutines
    fun appBarFlow(): Flow<HomeAppBarUiState>
}

interface DataProvider {
    @NativeCoroutines
    fun uiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}

/**
 * Provides data for the Discover UI components.
 */
interface DiscoverUiDataProvider : DataProvider {
    @NativeCoroutines
    fun uiDataFlow(): Flow<DiscoverUiState>
}

/**
 * Provides data for the Track UI components.
 */
interface TrackUiDataProvider : DataProvider {
    @NativeCoroutines
    fun uiDataFlow(): Flow<TrackUiState>
}

interface SettingUiDataProvider : DataProvider {
    @NativeCoroutines
    fun uiDataFlow(): Flow<SettingUiState>
}

interface DetailMediaUiDataProvider : DataProvider {
    val mediaId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailUiState>
}

interface DetailStaffUiDataProvider : DataProvider {
    val staffId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailStaffUiState>
}

interface DetailCharacterUiDataProvider : DataProvider {
    val characterId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailCharacterUiState>
}

interface TrackProgressDialogDataProvider : DataProvider {
    val mediaId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<TrackProgressDialogState>
}

interface DetailStudioUiDataProvider : DataProvider {
    val studioId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailStudioState>
}
