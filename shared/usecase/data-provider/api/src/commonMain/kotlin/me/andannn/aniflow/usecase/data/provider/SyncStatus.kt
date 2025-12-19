/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AppErrorHandler

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

fun AppErrorHandler.submitErrorOfSyncStatus(status: SyncStatus) {
    if (status is SyncStatus.Idle) {
        submitError(status.errors)
    }
}
