/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope

/**
 * Provide top level NavAnimatedContentScope.
 *
 * Can not get top level [AnimatedContentScope] in nested NavDisplay Using [LocalNavAnimatedContentScope].
 * Use [LocalTopNavAnimatedContentScope] instead.
 */
val LocalTopNavAnimatedContentScope =
    androidx.compose.runtime.staticCompositionLocalOf<AnimatedContentScope> {
        error("No LocalTopNavAnimatedContentScope provided")
    }

@Composable
fun <T : Any> rememberTopNavAnimatedContentScopeNavEntryDecorator(): NavEntryDecorator<T> =
    remember {
        topNavAnimatedContentScopeNavEntryDecorator()
    }

private fun <T : Any> topNavAnimatedContentScopeNavEntryDecorator(): NavEntryDecorator<T> {
    val onPop: (Any) -> Unit = { contentKey ->
    }

    return NavEntryDecorator(onPop = onPop) { entry ->
        val isDialog = entry.metadata.get("dialog") != null
        if (isDialog) {
            entry.Content()
        } else {
            val value = LocalNavAnimatedContentScope.current
            CompositionLocalProvider(
                LocalTopNavAnimatedContentScope provides value,
            ) {
                entry.Content()
            }
        }
    }
}
