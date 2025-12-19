/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

internal class SyncUserConditionTask :
    SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val authRepo: AuthRepository by inject()

    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) {
        Napier.d(tag = TAG) { "SyncUserConditionTask run" }
        authRepo.getAuthedUserFlow().distinctUntilChanged().collectLatest { authedUser ->
            if (authedUser != null) {
                with(mediaLibraryDao) {
                    doRefreshIfNeeded2(
                        TaskRefreshKey.SyncUserCondition(authedUser.id),
                        forceRefresh,
                    ) {
                        authRepo.syncUserCondition()
                    }
                }
            }
        }
    }
}
