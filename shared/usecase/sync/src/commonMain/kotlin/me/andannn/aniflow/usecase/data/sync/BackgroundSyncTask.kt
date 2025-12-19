/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.sync

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines

/**
 * A background sync task that can be scheduled to run in the background
 */
interface BackgroundSyncTask<T> {
    @NativeCoroutines
    suspend fun sync(): SyncResult<T>
}
