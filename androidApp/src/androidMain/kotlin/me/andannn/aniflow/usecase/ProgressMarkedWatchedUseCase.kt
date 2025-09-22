/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase

import android.util.Log
import androidx.compose.material3.SnackbarResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AppErrorHandler
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SnackBarMessage
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.util.SnackbarHostStateHolder
import me.andannn.aniflow.util.showSnackBarMessage

private const val TAG = "ProgressMarkedWatched"

context(
    snackbarHost: SnackbarHostStateHolder,
    mediaRepository: MediaRepository,
    errorHandler: AppErrorHandler,
)
suspend fun onMarkProgress(
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
            snackbarHost.showSnackBarMessage(
                if (isCompleted) {
                    SnackBarMessage.MediaMarkCompleted(title, newProgress)
                } else {
                    SnackBarMessage.MediaMarkWatched(title, newProgress)
                },
            )
        if (result == SnackbarResult.ActionPerformed) {
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
