/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.database.MediaLibraryDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

internal class SyncMediaListItemOfAuthedUserTask(
    private val mediaItemId: String,
) : SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val authRepo: AuthRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) =
        with(mediaLibraryDao) {
            Napier.d(tag = TAG) { "SyncMediaListItemOfAuthedUserTask run $mediaItemId" }

            authRepo.getAuthedUserFlow().distinctUntilChanged().collectLatest { authedUser ->
                if (authedUser != null) {
                    Napier.d(tag = TAG) { "SyncMediaListItemOfAuthedUserTask sync $mediaItemId, authedUser $authedUser" }
                    val key = TaskRefreshKey.SyncMediaListItem(userId = authedUser.id, mediaItemId)
                    doRefreshIfNeeded2(
                        key,
                        forceRefresh,
                    ) {
                        coroutineScope {
                            mediaRepo
                                .syncMediaListItemOfUser(this, authedUser.id, mediaItemId)
                                .await()
                                ?.toError()
                        }
                    }
                }
            }
        }
}
