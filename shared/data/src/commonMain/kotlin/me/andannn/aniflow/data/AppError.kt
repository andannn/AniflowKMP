/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Represents an error that can occur in the application.
 * Because Swift can not handle kotlin throwable, we use a sealed class to represent errors.
 *
 * @property message A human-readable message describing the error.
 */
sealed class AppError(
    open val message: String,
) {
    data class ServerError(
        override val message: String,
        val statusCode: Int,
    ) : AppError(message)

    data object NetworkConnectionError : AppError("No internet connection")

    data class OtherError(
        override val message: String,
    ) : AppError(message)
}

interface AppErrorHandler {
    fun submitError(error: List<AppError>)

    fun submitError(error: AppError)
}

interface AppErrorSource {
    val errorSharedFlow: SharedFlow<List<AppError>>
}

interface ErrorChannel :
    AppErrorSource,
    AppErrorHandler

fun AppErrorHandler.submitErrorOfSyncStatus(status: SyncStatus) {
    if (status is SyncStatus.Idle) {
        submitError(status.errors)
    }
}

fun buildErrorChannel(): ErrorChannel = ErrorChannelImpl()

internal class ErrorChannelImpl : ErrorChannel {
    override val errorSharedFlow: SharedFlow<List<AppError>> = MutableSharedFlow(replay = 1)

    override fun submitError(error: List<AppError>) {
        (errorSharedFlow as MutableSharedFlow<List<AppError>>).tryEmit(error)
    }

    override fun submitError(error: AppError) {
        submitError(listOf(error))
    }
}
