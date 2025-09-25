/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.TrackProgressDialogDataProvider
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.TrackProgressDialogState
import me.andannn.aniflow.data.model.define.MediaStatus

internal class TrackProgressDialogDataProviderImpl(
    override val mediaId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : TrackProgressDialogDataProvider {
    override fun uiDataFlow(): Flow<TrackProgressDialogState> =
        flow {
            val mediaModel = mediaRepository.getMediaFlow(mediaId).firstOrNull()
            val user =
                authRepository.getAuthedUserFlow().firstOrNull() ?: error("no user logged in")
            val listItem =
                mediaRepository
                    .getMediaListItemOfUserFlow(
                        userId = user.id,
                        mediaId = mediaId,
                    ).first()

            val nextAiringEp = mediaModel?.nextAiringEpisode?.episode
            val totalEp = mediaModel?.episodes

            val max =
                if (mediaModel?.status == MediaStatus.NOT_YET_RELEASED) {
                    0
                } else if (nextAiringEp != null) {
                    if (totalEp == null) nextAiringEp - 1 else minOf(nextAiringEp - 1, totalEp)
                } else {
                    totalEp
                }
            val initialProgress = listItem?.progress ?: 0

            emit(TrackProgressDialogState(max, initialProgress))
        }

    override fun uiSideEffect(forceRefreshFirstTime: Boolean) =
        createSideEffectFlow(
            forceRefreshFirstTime,
        )
}
