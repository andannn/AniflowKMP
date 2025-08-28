/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.DialogSceneStrategy
import androidx.navigation3.ui.DialogSceneStrategy.Companion.dialog
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.SinglePaneSceneStrategy
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator

@Composable
fun App() {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val navigator = remember { RootNavigator(backStack) }
    CompositionLocalProvider(
        LocalRootNavigator provides navigator,
    ) {
        NavDisplay(
            modifier = Modifier,
            backStack = backStack,
            sceneStrategy = DialogSceneStrategy<Screen>() then SinglePaneSceneStrategy(),
            entryDecorators =
                listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            entryProvider =
                entryProvider {
                    entry<Screen.Home> {
                        Home()
                    }

                    entry<Screen.MediaCategoryList> {
                        MediaCategoryPaging(
                            category = it.category,
                        )
                    }

                    entry<Screen.Notification> {
                        Notification()
                    }

                    entry<Screen.Dialog.Login>(
                        metadata = dialog(),
                    ) {
                        LoginDialog()
                    }
                },
        )
    }
}

val LocalRootNavigator =
    androidx.compose.runtime.staticCompositionLocalOf<RootNavigator> {
        error("No RootNavigator provided")
    }

class RootNavigator(
    private val backStack: SnapshotStateList<Screen>,
) {
    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun popBackStack() {
        with(backStack) {
            if (isNotEmpty()) {
                removeAt(lastIndex)
            }
        }
    }
}
