/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncDetailMediaTask(
    private val mediaItemId: String,
) : SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) =
        with(mediaLibraryDao) {
            Napier.d(tag = TAG) { "SyncMediaListItemTask run" }

            val key = TaskRefreshKey.SyncDetailMediaItem(mediaItemId)
            doRefreshIfNeeded2(
                key,
                forceRefresh,
            ) {
                coroutineScope {
                    mediaRepo
                        .syncDetailMedia(
                            this,
                            mediaId = mediaItemId,
                            voiceActorLanguage = StaffLanguage.JAPANESE,
                        ).await()
                }
            }
        }
}
