/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.supervisorScope
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.toMediaType
import me.andannn.aniflow.database.MediaLibraryDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

internal class RefreshAllCategoriesTask :
    SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    private fun createKey(mediaContentMode: MediaContentMode): TaskRefreshKey =
        TaskRefreshKey.AllCategories(mediaContentMode = mediaContentMode)

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) {
        Napier.d(tag = TAG) { "RefreshAllCategoriesTask run" }

        var innerForceRefresh = forceRefresh
        with(mediaLibraryDao) {
            mediaRepo
                .getContentModeFlow()
                .collectLatest { mode ->
                    doRefreshIfNeeded(createKey(mode), innerForceRefresh) {
                        val categories = mode.toMediaType().allCategories()
                        supervisorScope {
                            val deferredList =
                                categories.map { category ->
                                    mediaRepo.syncMediaCategory(this, category)
                                }

                            deferredList
                                .awaitAll()
                                .filterNotNull()
                                .map(Throwable::toError)
                                .distinct()
                        }
                    }
                    innerForceRefresh = false
                }
        }
    }
}
