/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.paging

import me.andannn.aniflow.data.AppError

sealed interface LoadingStatus {
    data object Idle : LoadingStatus

    data object AllLoaded : LoadingStatus

    data object Loading : LoadingStatus

    data class Error(
        val error: AppError,
    ) : LoadingStatus
}
