/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.navEntryDecorator

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

@Composable
fun rememberErrorHandlerNavEntryDecorator(): NavEntryDecorator<Any> = remember { snackbarHostStateHolderNavEntryDecorator() }

private fun snackbarHostStateHolderNavEntryDecorator(): NavEntryDecorator<Any> {
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
