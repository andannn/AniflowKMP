/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import me.andannn.aniflow.data.model.MediaModel

enum class SnackbarShowDuration {
    Short,
    Long,
    Indefinite,
}

sealed class SnackBarMessage(
    open val message: String,
    open val duration: SnackbarShowDuration = SnackbarShowDuration.Short,
    open val actionLabel: String? = null,
    open val withDismissAction: Boolean = false,
) {
    data object NoNetWorkConnectionError : SnackBarMessage(
        message = "No network connection",
    )

    data object ToManyRequestsError : SnackBarMessage(
        message = "To many requests, please try again later",
    )

    data class MediaMarkWatched(
        val title: String,
        val ep: Int,
    ) : SnackBarMessage(
            message = "Ep $ep of $title marked as watched",
            actionLabel = "Undo",
        )

    data class MediaMarkCompleted(
        val title: String,
        val ep: Int,
    ) : SnackBarMessage(
            message = "Ep $ep of $title marked as completed",
            actionLabel = "Undo",
        )

    data object ScoreSaved : SnackBarMessage(
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
    ) : SnackBarMessage(message)

    data class FallBackRemoteError(
        override val message: String,
    ) : SnackBarMessage(
            "Unknown error",
        )
}

fun AppError.toAlert() =
    when (this) {
        is AppError.ServerError -> SnackBarMessage.ServerError(statusCode, message)
        AppError.NetworkConnectionError -> SnackBarMessage.NoNetWorkConnectionError
        is AppError.OtherError -> SnackBarMessage.FallBackRemoteError(message)
    }
