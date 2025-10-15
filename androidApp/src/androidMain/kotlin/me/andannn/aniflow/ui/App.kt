/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.DialogSceneStrategy.Companion.dialog
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import me.andannn.aniflow.util.rememberErrorHandlerNavEntryDecorator

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
                    rememberErrorHandlerNavEntryDecorator(),
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

                    entry<Screen.Search> {
                        Search()
                    }

                    entry<Screen.Settings> {
                        Settings()
                    }

                    entry<Screen.DetailMedia> {
                        DetailMedia(it.mediaId)
                    }

                    entry<Screen.DetailStaff> {
                        DetailStaff(it.staffId)
                    }

                    entry<Screen.DetailCharacter> {
                        DetailCharacter(it.characterId)
                    }

                    entry<Screen.DetailStudio> {
                        DetailStudio(it.studioId)
                    }

                    entry<Screen.DetailStaffPaging> {
                        DetailMediaStaffPaging(it.mediaId)
                    }

                    entry<Screen.DetailCharacterPaging> {
                        DetailMediaCharacterPaging(it.mediaId)
                    }

                    entry<Screen.Dialog.ScoringDialog>(
                        metadata = dialog(),
                    ) {
                        ScoringDialog(it.mediaId)
                    }

                    entry<Screen.Dialog.Login>(
                        metadata = dialog(),
                    ) {
                        LoginDialog()
                    }

                    entry<Screen.Dialog.SettingOption>(
                        metadata = dialog(),
                    ) {
                        SettingOptionDialog(it.settingItem)
                    }

                    entry<Screen.Dialog.TrackProgressDialog>(
                        metadata = dialog(),
                    ) {
                        TrackProgressDialog(
                            mediaId = it.mediaId,
                        )
                    }

                    entry<Screen.Dialog.PresentationDialog>(
                        metadata =
                            dialog(
                                dialogProperties =
                                    DialogProperties(
                                        dismissOnBackPress = false,
                                        dismissOnClickOutside = false,
                                    ),
                            ),
                    ) {
                        PresentationModeLoginDialog()
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
    private val backStack: NavBackStack<NavKey>,
) {
    val backStackList: List<NavKey>
        get() = backStack

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun popBackStack() {
        with(backStack) {
            if (backStack.size > 1) {
                removeAt(lastIndex)
            }
        }
    }
}
