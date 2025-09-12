/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.DialogSceneStrategy
import androidx.navigation3.ui.DialogSceneStrategy.Companion.dialog
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.SinglePaneSceneStrategy
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import me.andannn.aniflow.util.rememberErrorHandlerNavEntryDecorator
import me.andannn.aniflow.util.rememberResultStoreNavEntryDecorator

@Composable
fun App(navigator: RootNavigator) {
    CompositionLocalProvider(
        LocalRootNavigator provides navigator,
    ) {
        NavDisplay(
            modifier = Modifier,
            backStack = navigator.backStackList,
            sceneStrategy = DialogSceneStrategy<NavKey>() then SinglePaneSceneStrategy(),
            entryDecorators =
                listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                    rememberResultStoreNavEntryDecorator(),
                    rememberErrorHandlerNavEntryDecorator(),
                ),
            entryProvider =
                entryProvider {
                    entry<Screen.Home>(clazzContentKey = { it.toJson() }) {
                        Home()
                    }

                    entry<Screen.MediaCategoryList>(clazzContentKey = { it.toJson() }) {
                        MediaCategoryPaging(
                            category = it.category,
                        )
                    }

                    entry<Screen.Notification>(clazzContentKey = { it.toJson() }) {
                        Notification()
                    }

                    entry<Screen.Search>(clazzContentKey = { it.toJson() }) {
                        Search()
                    }

                    entry<Screen.Settings>(clazzContentKey = { it.toJson() }) {
                        Settings()
                    }

                    entry<Screen.Dialog.Login>(
                        metadata = dialog(),
                        clazzContentKey = { it.toJson() },
                    ) {
                        LoginDialog()
                    }

                    entry<Screen.Dialog.SettingOption>(
                        metadata = dialog(),
                        clazzContentKey = { it.toJson() },
                    ) {
                        SettingOptionDialog(it.settingItem)
                    }
                },
        )
    }
}

val LocalRootNavigator =
    androidx.compose.runtime.staticCompositionLocalOf<RootNavigator> {
        error("No RootNavigator provided")
    }

class RootNavigator constructor(
    private val backStack: SnapshotStateList<NavKey>,
) {
    val backStackList: List<NavKey>
        get() = backStack

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
