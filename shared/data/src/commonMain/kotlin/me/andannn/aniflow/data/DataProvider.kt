/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.DetailCharacterUiState
import me.andannn.aniflow.data.model.DetailStaffUiState
import me.andannn.aniflow.data.model.DetailUiState
import me.andannn.aniflow.data.model.DiscoverUiState
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.SettingUiState
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

/**
 * Provides data for the Discover UI components.
 */
interface DiscoverUiDataProvider {
    @NativeCoroutines
    fun discoverUiDataFlow(): Flow<DiscoverUiState>

    @NativeCoroutines
    fun discoverUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}

/**
 * Provides data for the Track UI components.
 */
interface TrackUiDataProvider {
    @NativeCoroutines
    fun trackUiDataFlow(): Flow<TrackUiState>

    @NativeCoroutines
    fun trackUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}

interface SettingUiDataProvider {
    @NativeCoroutines
    fun settingUiDataFlow(): Flow<SettingUiState>

    @NativeCoroutines
    fun settingUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}

interface DetailMediaUiDataProvider {
    val mediaId: String

    @NativeCoroutines
    fun detailUiDataFlow(): Flow<DetailUiState>

    @NativeCoroutines
    fun detailUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}

interface DetailStaffUiDataProvider {
    val staffId: String

    fun detailUiDataFlow(): Flow<DetailStaffUiState>

    fun detailUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}

interface DetailCharacterUiDataProvider {
    val characterId: String

    fun detailUiDataFlow(): Flow<DetailCharacterUiState>

    fun detailUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus>
}
