/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable

private const val TAG = "Home"

@Serializable
private sealed interface HomeNestedScreen {
    @Serializable
    data object Discover : HomeNestedScreen

    @Serializable
    data object Track : HomeNestedScreen

    @Serializable
    data object Social : HomeNestedScreen

    @Serializable
    data object Profile : HomeNestedScreen
}

@Composable
fun Home(modifier: Modifier = Modifier) {
    val navigator =
        remember {
            NestedNavigator(
                mutableStateListOf(HomeNestedScreen.Discover),
            )
        }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationArea(
                selected = navigator.currentTopLevelNavigation,
                onItemClick = { item ->
                    navigator.navigateTo(item)
                },
            )
        },
        content = { paddingValues ->
            NestNavigation(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                nestedBackStack = navigator.backStack,
            )
        },
    )
}

@Composable
private fun NestNavigation(
    modifier: Modifier = Modifier,
    nestedBackStack: SnapshotStateList<HomeNestedScreen>,
) {
    NavDisplay(
        modifier = modifier,
        backStack = nestedBackStack,
        entryDecorators =
            listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider =
            entryProvider {
                entry(HomeNestedScreen.Discover) {
                    Discover()
                }
                entry(HomeNestedScreen.Track) {
                    Track()
                }
            },
    )
}

@Composable
private fun NavigationArea(
    selected: TopLevelNavigation,
    modifier: Modifier = Modifier,
    onItemClick: (TopLevelNavigation) -> Unit = {},
) {
    NavigationBar(modifier = modifier) {
        TopLevelNavigation.entries.forEach { item ->
            NavigationBarItem(
                selected = selected == item,
                label = { Text(item.label) },
                icon = {
                    if (selected == item) {
                        Icon(item.selectedIcon, contentDescription = null)
                    } else {
                        Icon(item.unselectedIcon, contentDescription = null)
                    }
                },
                onClick = { onItemClick(item) },
            )
        }
    }
}

enum class TopLevelNavigation {
    DISCOVER,
    TRACK,
    SOCIAL,
    PROFILE,
}

private val TopLevelNavigation.selectedIcon
    get() =
        when (this) {
            TopLevelNavigation.DISCOVER -> Icons.Default.Explore
            TopLevelNavigation.TRACK -> Icons.Default.CollectionsBookmark
            TopLevelNavigation.SOCIAL -> Icons.Default.Forum
            TopLevelNavigation.PROFILE -> Icons.Default.Person
        }

private val TopLevelNavigation.unselectedIcon
    get() =
        when (this) {
            TopLevelNavigation.DISCOVER -> Icons.Outlined.Explore
            TopLevelNavigation.TRACK -> Icons.Outlined.CollectionsBookmark
            TopLevelNavigation.SOCIAL -> Icons.Outlined.Forum
            TopLevelNavigation.PROFILE -> Icons.Outlined.Person
        }

private val TopLevelNavigation.label
    get() =
        when (this) {
            TopLevelNavigation.DISCOVER -> "Discover"
            TopLevelNavigation.TRACK -> "Track"
            TopLevelNavigation.SOCIAL -> "Social"
            TopLevelNavigation.PROFILE -> "Profile"
        }

private class NestedNavigator(
    val backStack: SnapshotStateList<HomeNestedScreen>,
) {
    val currentTopLevelNavigation: TopLevelNavigation
        get() =
            backStack.lastOrNull()?.toTopLevelNavigation()
                ?: TopLevelNavigation.DISCOVER

    fun navigateTo(topLevelNavigation: TopLevelNavigation) {
        val toScreen = topLevelNavigation.toScreen()
        Napier.d(tag = TAG) { "Navigated to $toScreen before : ${backStack.toList()}" }
        if (backStack.lastOrNull() == toScreen) {
            Napier.d(tag = TAG) { "Already on $topLevelNavigation" }
            return
        }

        backStack.clear()
        backStack.add(HomeNestedScreen.Discover)

        if (toScreen != HomeNestedScreen.Discover) {
            backStack.add(toScreen)
        }

        Napier.d(tag = TAG) { "Navigated to $toScreen after : ${backStack.toList()}" }
    }

    private fun TopLevelNavigation.toScreen() =
        when (this) {
            TopLevelNavigation.DISCOVER -> HomeNestedScreen.Discover
            TopLevelNavigation.TRACK -> HomeNestedScreen.Track
            TopLevelNavigation.SOCIAL -> HomeNestedScreen.Social
            TopLevelNavigation.PROFILE -> HomeNestedScreen.Profile
        }

    fun HomeNestedScreen.toTopLevelNavigation() =
        when (this) {
            HomeNestedScreen.Discover -> TopLevelNavigation.DISCOVER
            HomeNestedScreen.Profile -> TopLevelNavigation.PROFILE
            HomeNestedScreen.Social -> TopLevelNavigation.SOCIAL
            HomeNestedScreen.Track -> TopLevelNavigation.TRACK
        }
}
