/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.navEntryDecorator
import io.github.aakira.napier.Napier
import me.andannn.aniflow.BuildConfig
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AppErrorSource

private const val TAG = "ErrorHandler"

val LocalErrorHandler =
    androidx.compose.runtime.staticCompositionLocalOf<ErrorHandler> {
        error("No RootNavigator provided")
    }

@Composable
fun ErrorHandleSideEffect(
    source: AppErrorSource,
    errorHandler: ErrorHandler = LocalErrorHandler.current,
) {
    LaunchedEffect(source) {
        source.errorSharedFlow.collect {
            it.toSet().forEach {
                errorHandler.handleError(it)
            }
        }
    }
}

class ErrorHandler {
    var snackBarHost: SnackbarHostState? = null

    suspend fun handleError(error: AppError): ErrorHandleResult {
        Napier.d(tag = TAG) { "handleError: error: $error" }
        val snackBarMessage = error.toAlert()
        val result =
            snackBarHost?.let { host ->
                val result = host.showSnackbar(snackBarMessage.toSnackbarVisuals())
                ErrorHandleResult.SnackBarHandleResult(snackBarMessage, result)
            } ?: error("snackbar host is not set")

        return result
    }

    fun dispose() {
        snackBarHost = null
    }
}

@Composable
fun rememberErrorHandlerNavEntryDecorator(): NavEntryDecorator<Any> = remember { errorHandlerNavEntryDecorator() }

@Composable
fun rememberSnackBarHostState(errorHandler: ErrorHandler = LocalErrorHandler.current): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(snackbarHostState) {
        errorHandler.snackBarHost = snackbarHostState

        onDispose {
            errorHandler.dispose()
        }
    }

    return snackbarHostState
}

sealed interface ErrorHandleResult {
    data class SnackBarHandleResult(
        val snackBarMessage: SnackBarMessage,
        val snackbarResult: SnackbarResult,
    ) : ErrorHandleResult
}

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

private fun AppError.toAlert() =
    when (this) {
        is AppError.ServerError -> SnackBarMessage.ServerError(statusCode, message)
        is AppError.OtherError -> SnackBarMessage.FallBackRemoteError(message)
    }

private fun errorHandlerNavEntryDecorator(): NavEntryDecorator<Any> {
    val onPop: (Any) -> Unit = { contentKey ->
    }

    return navEntryDecorator(onPop = onPop) { entry ->
        val errorHandler = remember { ErrorHandler() }
        CompositionLocalProvider(
            LocalErrorHandler provides errorHandler,
        ) {
            entry.Content()
        }
    }
}
