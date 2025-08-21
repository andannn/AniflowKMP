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
import me.andannn.aniflow.data.model.define.toMediaType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.getValue

private const val TAG = "RefreshAllCategoriesTas"

internal class RefreshAllCategoriesTask :
    SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()

    override val tag: String = "RefreshAllCategoriesTask"

    override suspend fun WrappedProducerScope<SyncStatus>.run() {
        Napier.d(tag = TAG) { "RefreshAllCategoriesTask run" }

        mediaRepo
            .getContentModeFlow()
            .collectLatest { mode ->
                try {
                    send(SyncStatus.Loading)

                    val categories = mode.toMediaType().allCategories()
                    supervisorScope {
                        val deferredList =
                            categories.map { category ->
                                mediaRepo.syncMediaCategory(this, category)
                            }
                        val errors =
                            deferredList
                                .awaitAll()
                                .filterNotNull()
                                .map(Throwable::toError)
                                .distinct()

                        send(SyncStatus.Idle(errors))
                    }
                } catch (cancel: CancellationException) {
                    // Task was cancelled, do nothing
                    Napier.d(tag = TAG) { "RefreshAllCategoriesTask cancelled" }
                    send(SyncStatus.Idle())
                }
            }
    }
}
