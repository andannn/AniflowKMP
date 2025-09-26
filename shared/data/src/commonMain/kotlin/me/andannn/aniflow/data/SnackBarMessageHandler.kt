/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class SharedSnackbarResult {
    Dismissed,
    ActionPerformed,
}

interface SnackBarMessageHandler {
    fun showSnackBarMessage(
        message: SnackBarMessage,
        callBack: (SharedSnackbarResult) -> Unit = {},
    )
}

internal suspend fun SnackBarMessageHandler.showSnackBarMessageSuspend(message: SnackBarMessage): SharedSnackbarResult =
    suspendCoroutine { cont ->
        showSnackBarMessage(message) {
            cont.resume(it)
        }
    }
