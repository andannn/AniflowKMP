/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import me.andannn.aniflow.platform.SnackBarMessage
import me.andannn.aniflow.platform.SnackbarShowDuration

sealed class SnackBarMessageDetail(
    override val message: String,
    override val duration: SnackbarShowDuration = SnackbarShowDuration.Short,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
) : SnackBarMessage {
    data object NoNetWorkConnectionError : SnackBarMessageDetail(
        message = "No network connection",
    )

    data object ToManyRequestsError : SnackBarMessageDetail(
        message = "To many requests, please try again later",
    )

    data class MediaMarkWatched(
        val title: String,
        val ep: Int,
    ) : SnackBarMessageDetail(
            message = "Ep $ep of $title marked as watched",
            actionLabel = "Undo",
        )

    data class MediaMarkCompleted(
        val title: String,
        val ep: Int,
    ) : SnackBarMessageDetail(
            message = "Ep $ep of $title marked as completed",
            actionLabel = "Undo",
        )

    data class MediaMarkDropped(
        val title: String,
    ) : SnackBarMessageDetail(
            message = "$title marked as dropped",
            actionLabel = "Undo",
        )

    data object ScoreSaved : SnackBarMessageDetail(
        message = "Score saved",
    )

    /**
     * Server error when status code is not in 200..299.
     *
     * @property message The error message send from server.
     */
    data class ServerError(
        val statusCode: Int,
        override val message: String,
    ) : SnackBarMessageDetail(message)

    data class FallBackRemoteError(
        override val message: String,
    ) : SnackBarMessageDetail(
            "Unknown error",
        )
}

fun AppError.toAlert() =
    when (this) {
        is AppError.ServerError -> SnackBarMessageDetail.ServerError(statusCode, message)
        AppError.NetworkConnectionError -> SnackBarMessageDetail.NoNetWorkConnectionError
        is AppError.OtherError -> SnackBarMessageDetail.FallBackRemoteError(message)
    }
