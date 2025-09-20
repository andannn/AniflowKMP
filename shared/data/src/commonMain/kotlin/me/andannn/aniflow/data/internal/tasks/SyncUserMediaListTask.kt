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
import kotlinx.coroutines.flow.map
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.toMediaType
import me.andannn.aniflow.database.MediaLibraryDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncUserMediaListTask :
    SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val authRepo: AuthRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) {
        Napier.d(tag = TAG) { "SyncUserMediaListTask run" }

        with(mediaLibraryDao) {
            val refreshParamFlow =
                combine(
                    authRepo.getAuthedUserFlow(),
                    mediaRepo.getContentModeFlow(),
                    authRepo.getUserOptionsFlow().map { it.scoreFormat },
                ) { authedUser, contentMode, scoreFormat ->
                    RefreshParam(
                        authedUser,
                        contentMode,
                        scoreFormat,
                    )
                }.distinctUntilChanged()

            var innerForceRefresh = forceRefresh
            refreshParamFlow.collectLatest { (authedUser, contentMode, scoreFormat) ->
                if (authedUser != null) {
                    val refreshKey =
                        TaskRefreshKey.SyncUserMediaList(contentMode, authedUser.id, scoreFormat)
                    doRefreshIfNeeded2(refreshKey, innerForceRefresh) {
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
                                    scoreFormat = scoreFormat,
                                )
                            }

                        deferred.await()?.toError()
                    }
                }
                innerForceRefresh = false
            }
        }
    }
}

private data class RefreshParam(
    val authedUser: UserModel?,
    val contentMode: MediaContentMode,
    val scoreFormat: ScoreFormat,
)
