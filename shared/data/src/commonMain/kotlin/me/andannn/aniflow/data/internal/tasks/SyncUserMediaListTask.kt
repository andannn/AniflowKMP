/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.toMediaType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.getValue

private const val TAG = "SyncUserMediaListTask"

internal class SyncUserMediaListTask :
    SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val authRepo: AuthRepository by inject()

    override val tag: String = "SyncUserMediaListTask"

    override suspend fun WrappedProducerScope<SyncStatus>.run() {
        Napier.d(tag = TAG) { "SyncUserMediaListTask run" }
        combine(
            authRepo.getAuthedUserFlow(),
            mediaRepo.getContentModeFlow(),
        ) { authedUser, contentMode -> Pair(authedUser, contentMode) }
            .distinctUntilChanged()
            .collectLatest { (authedUser, contentMode) ->
                if (authedUser != null) {
                    try {
                        send(SyncStatus.Loading)
                        val deferred =
                            coroutineScope {
                                mediaRepo.syncMediaListByUserId(
                                    scope = this,
                                    userId = authedUser.id,
                                    status =
                                        listOf(
                                            MediaListStatus.PLANNING,
                                            MediaListStatus.CURRENT,
                                        ),
                                    mediaType = contentMode.toMediaType(),
                                )
                            }

                        val error = deferred.await()

                        if (error == null) {
                            send(SyncStatus.Idle())
                        } else {
                            send(SyncStatus.Idle(listOf(error.toError())))
                        }
                    } catch (cancelled: CancellationException) {
                        // Task was cancelled, do nothing
                        Napier.d(tag = TAG) { "SyncUserMediaListTask cancelled" }
                        send(SyncStatus.Idle())
                    }
                }
            }
    }
}
