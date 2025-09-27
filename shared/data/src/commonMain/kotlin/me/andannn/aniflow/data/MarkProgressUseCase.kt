/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import org.koin.core.component.KoinComponent

private const val TAG = "MarkProgressUseCase"

object MarkProgressUseCase : KoinComponent {
    private val mediaRepository: MediaRepository = getKoin().get()
    private val authRepository: AuthRepository = getKoin().get()

    @NativeCoroutines
    suspend fun markProgress(
        mediaListModel: MediaListModel,
        mediaModel: MediaModel,
        newProgress: Int,
        snackBarMessageHandler: SnackBarMessageHandler,
        errorHandler: AppErrorHandler,
    ) {
        val titleLanguage: UserTitleLanguage = authRepository.getUserOptionsFlow().first().titleLanguage
        context(snackBarMessageHandler, mediaRepository, errorHandler) {
            onMarkProgress(
                mediaListModel,
                mediaModel,
                newProgress,
                titleLanguage,
            )
        }
    }
}

context(
    snackBarMessageHandler: SnackBarMessageHandler,
    mediaRepository: MediaRepository,
    errorHandler: AppErrorHandler,
)
private suspend fun onMarkProgress(
    mediaListModel: MediaListModel,
    mediaModel: MediaModel,
    newProgress: Int,
    titleLanguage: UserTitleLanguage,
) {
    if (newProgress == mediaListModel.progress) return

    val oldListItem = mediaListModel
    val isCompleted = newProgress == mediaModel.episodes
    val newStatus = if (isCompleted) MediaListStatus.COMPLETED else MediaListStatus.CURRENT
    Napier.d(tag = TAG) {
        "onMarkProgress: mediaId: ${mediaModel.id}, isCompleted: $isCompleted, newProgress: $newProgress, newStatus $newStatus"
    }

    val error =
        mediaRepository.updateMediaListStatus(
            mediaListId = oldListItem.id,
            status = newStatus,
            progress = newProgress,
        )

    Napier.d(tag = TAG) { "onMarkProgress: completed error: $error" }

    if (error == null) {
        val title =
            mediaModel.title.getUserTitleString(titleLanguage)
        val result =
            snackBarMessageHandler.showSnackBarMessageSuspend(
                if (isCompleted) {
                    SnackBarMessage.MediaMarkCompleted(title, newProgress)
                } else {
                    SnackBarMessage.MediaMarkWatched(title, newProgress)
                },
            )
        if (result == SharedSnackbarResult.ActionPerformed) {
            // Undo action performed.
            Napier.d(tag = TAG) { "onMarkProgress: Undo performed E" }
            val error =
                mediaRepository.updateMediaListStatus(
                    mediaListId = oldListItem.id,
                    status = oldListItem.status,
                    progress = oldListItem.progress,
                )
            Napier.d(tag = TAG) { "onMarkProgress: Undo performed X. error: $error" }
            if (error != null) errorHandler.submitError(error)
        }
    } else {
        errorHandler.submitError(error)
    }
}
