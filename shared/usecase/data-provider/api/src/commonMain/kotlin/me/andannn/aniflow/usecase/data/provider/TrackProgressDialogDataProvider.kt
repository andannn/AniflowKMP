/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow

data class TrackProgressDialogState(
    val maxEp: Int? = null,
    val initialProgress: Int = 0,
) {
    companion object {
        val Empty = TrackProgressDialogState()
    }
}

interface TrackProgressDialogDataProvider : DataProvider {
    val mediaId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<TrackProgressDialogState>
}
