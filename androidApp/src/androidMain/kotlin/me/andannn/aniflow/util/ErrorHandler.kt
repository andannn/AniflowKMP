/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AppErrorSource
import me.andannn.aniflow.data.SnackBarMessage
import me.andannn.aniflow.data.SnackbarShowDuration
import me.andannn.aniflow.data.toAlert

private const val TAG = "ErrorHandler"

@Composable
fun ErrorHandleSideEffect(source: AppErrorSource) {
    val snackBarHolder = LocalSnackbarHostStateHolder.current
    val errorHandler =
        remember(snackBarHolder) {
            ErrorHandler(snackBarHolder)
        }

    LaunchedEffect(source) {
        source.errorSharedFlow.collect {
            it.forEach {
                errorHandler.handleError(it)
            }
        }
    }
}

class ErrorHandler(
    private val snackBarHolder: SnackbarHostStateHolder,
) {
    suspend fun handleError(error: AppError): ErrorHandleResult {
        Napier.d(tag = TAG) { "handleError: error: $error" }
        val snackBarMessage = error.toAlert()
        val result =
            snackBarHolder.snackBarHost?.let { host ->
                val result = host.showSnackbar(snackBarMessage.toSnackbarVisuals())
                ErrorHandleResult.SnackBarHandleResult(snackBarMessage, result)
            } ?: error("snackbar host is not set")

        return result
    }
}

sealed interface ErrorHandleResult {
    data class SnackBarHandleResult(
        val snackBarMessage: SnackBarMessage,
        val snackbarResult: SnackbarResult,
    ) : ErrorHandleResult
}
