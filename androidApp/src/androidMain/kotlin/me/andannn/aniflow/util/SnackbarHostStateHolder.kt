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
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.navEntryDecorator
import me.andannn.aniflow.data.SnackBarMessage
import me.andannn.aniflow.data.SnackbarShowDuration

/**
 * A CompositionLocal to provide access to the SnackbarHostStateHolder.
 */
val LocalSnackbarHostStateHolder =
    androidx.compose.runtime.staticCompositionLocalOf<SnackbarHostStateHolder> {
        error("No RootNavigator provided")
    }

/**
 * A Composable function to remember and manage the lifecycle of a SnackbarHostState.
 *
 * It sets the current SnackbarHostState in the provided [holder] when composed and
 * clears it when disposed.
 */
@Composable
fun rememberSnackBarHostState(holder: SnackbarHostStateHolder = LocalSnackbarHostStateHolder.current): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }

    DisposableEffect(snackbarHostState) {
        holder.snackBarHost = snackbarHostState

        onDispose {
            holder.snackBarHost = null
        }
    }

    return snackbarHostState
}

/**
 * A holder class to keep a reference to the current SnackbarHostState.
 */
class SnackbarHostStateHolder {
    var snackBarHost: SnackbarHostState? = null
}

/**
 * A suspend function to show a Snackbar with the given message.
 */
suspend fun SnackbarHostStateHolder.showSnackBarMessage(message: SnackBarMessage): SnackbarResult =
    snackBarHost?.showSnackbar(message.toSnackbarVisuals())
        ?: error("snackBarHost is null")

fun SnackBarMessage.toSnackbarVisuals(): SnackbarVisuals {
    val actionLabel = actionLabel
    val duration = duration
    val message = message
    val withDismissAction = withDismissAction
    return object : SnackbarVisuals {
        override val actionLabel = actionLabel
        override val duration = duration.toSnackbarDuration()
        override val message = message
        override val withDismissAction = withDismissAction
    }
}

private fun SnackbarShowDuration.toSnackbarDuration(): SnackbarDuration =
    when (this) {
        SnackbarShowDuration.Short -> SnackbarDuration.Short
        SnackbarShowDuration.Long -> SnackbarDuration.Long
        SnackbarShowDuration.Indefinite -> SnackbarDuration.Indefinite
    }

@Composable
fun <T : Any> rememberErrorHandlerNavEntryDecorator(): NavEntryDecorator<T> = remember { snackbarHostStateHolderNavEntryDecorator() }

private fun <T : Any> snackbarHostStateHolderNavEntryDecorator(): NavEntryDecorator<T> {
    val onPop: (Any) -> Unit = { contentKey ->
    }

    return navEntryDecorator(onPop = onPop) { entry ->
        val holder = remember { SnackbarHostStateHolder() }
        CompositionLocalProvider(
            LocalSnackbarHostStateHolder provides holder,
        ) {
            entry.Content()
        }
    }
}
