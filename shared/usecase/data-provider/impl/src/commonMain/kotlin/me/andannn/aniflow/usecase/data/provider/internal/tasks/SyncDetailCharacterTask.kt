/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncDetailCharacterTask(
    private val characterId: String,
) : SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) =
        with(mediaLibraryDao) {
            Napier.d(tag = TAG) { "SyncDetailStaffTask E" }

            val key = TaskRefreshKey.SyncDetailCharacter(characterId)
            doRefreshIfNeeded2(
                key,
                forceRefresh,
            ) {
                Napier.d(tag = TAG) { "SyncDetailStaffTask in E" }
                coroutineScope {
                    mediaRepo
                        .syncDetailCharacter(
                            this,
                            characterId,
                        ).await()
                }
            }
            Napier.d(tag = TAG) { "SyncDetailStaffTask X" }
        }
}
