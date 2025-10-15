/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.database.MediaLibraryDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncDetailStudioTask(
    private val studioId: String,
) : SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) =
        with(mediaLibraryDao) {
            Napier.d(tag = TAG) { "SyncDetailStudioTask E" }

            val key = TaskRefreshKey.SyncDetailStudio(studioId)
            doRefreshIfNeeded2(
                key,
                forceRefresh,
            ) {
                Napier.d(tag = TAG) { "SyncDetailStudioTask in E" }
                coroutineScope {
                    mediaRepo
                        .syncDetailStudio(
                            this,
                            studioId,
                        ).await()
                        ?.toError()
                }
            }
            Napier.d(tag = TAG) { "SyncDetailStudioTask X" }
        }
}
