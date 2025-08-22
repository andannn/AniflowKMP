/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.DiscoverUiState
import me.andannn.aniflow.data.model.HomeAppBarUiState
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

/**
 * Represents an error that can occur in the application.
 * Because Swift can not handle kotlin throwable, we use a sealed class to represent errors.
 *
 * @property message A human-readable message describing the error.
 */
sealed class AppError(
    open val message: String,
) {
    data class RemoteSyncError(
        override val message: String,
    ) : AppError(message)

    data class OtherError(
        override val message: String,
    ) : AppError(message)
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
