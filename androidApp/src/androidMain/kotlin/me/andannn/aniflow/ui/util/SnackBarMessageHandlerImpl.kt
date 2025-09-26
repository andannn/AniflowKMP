/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.SnackBarMessage
import me.andannn.aniflow.data.SnackBarMessageHandler
import me.andannn.aniflow.data.SnackbarResult
import me.andannn.aniflow.util.SnackbarHostStateHolder
import me.andannn.aniflow.util.showSnackBarMessage

fun buildSnackBarMessageHandler(
    scope: CoroutineScope,
    snackbarHostStateHolder: SnackbarHostStateHolder,
): SnackBarMessageHandler = SnackBarMessageHandlerImpl(scope, snackbarHostStateHolder)

private class SnackBarMessageHandlerImpl(
    private val scope: CoroutineScope,
    private val snackbarHostStateHolder: SnackbarHostStateHolder,
) : SnackBarMessageHandler {
    override fun showSnackBarMessage(
        message: SnackBarMessage,
        callBack: (SnackbarResult) -> Unit,
    ) {
        scope.launch {
            val result = snackbarHostStateHolder.showSnackBarMessage(message).toResult()
            callBack(result)
        }
    }
}

private fun androidx.compose.material3.SnackbarResult.toResult(): SnackbarResult =
    when (this) {
        androidx.compose.material3.SnackbarResult.Dismissed -> SnackbarResult.Dismissed
        androidx.compose.material3.SnackbarResult.ActionPerformed -> SnackbarResult.ActionPerformed
    }
