/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import me.andannn.aniflow.BuildConfig

sealed class SnackBarMessage(
    private val message: String,
    private val duration: SnackbarDuration = SnackbarDuration.Short,
    private val actionLabel: String? = null,
    private val withDismissAction: Boolean = false,
) {
    data object NoNetWorkConnectionError : SnackBarMessage(
        message = "No network connection",
    )

    data object ToManyRequestsError : SnackBarMessage(
        message = "To many requests, please try again later",
    )

    /**
     * Server error when status code is not in 200..299.
     *
     * @property message The error message send from server.
     */
    data class ServerError(
        val statusCode: Int,
        val message: String,
    ) : SnackBarMessage(message + if (BuildConfig.DEBUG) "\nDebug info: $statusCode" else "")

    data class FallBackRemoteError(
        val message: String,
    ) : SnackBarMessage(
            "Unknown error" + if (BuildConfig.DEBUG) "\nDebug info: $message" else "",
        )

    fun toSnackbarVisuals(): SnackbarVisuals {
        val actionLabel = actionLabel
        val duration = duration
        val message = message
        val withDismissAction = withDismissAction
        return object : SnackbarVisuals {
            override val actionLabel = actionLabel
            override val duration = duration
            override val message = message
            override val withDismissAction = withDismissAction
        }
    }
}
