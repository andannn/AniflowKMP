/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.ui.theme.AppNameFontFamily
import me.andannn.aniflow.ui.widget.MediaContentSwitcher
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "Home"

@Serializable
sealed interface HomeNestedScreen {
    @Serializable
    data object Discover : HomeNestedScreen

    @Serializable
    data object Track : HomeNestedScreen

    @Serializable
    data object SearchInput : HomeNestedScreen
}

class HomeViewModel : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(homeViewModel: HomeViewModel = koinViewModel()) {
    HomeContent(
        navigator =
            remember {
                NestedNavigator(
                    mutableStateListOf(HomeNestedScreen.SearchInput),
                )
            },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    navigator: NestedNavigator,
) {
    Scaffold(
        modifier = modifier,
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
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .fillMaxSize(),
                navigator = navigator,
            )
        },
    )
}

@Composable
private fun NestNavigation(
    modifier: Modifier = Modifier,
    navigator: NestedNavigator,
) {
    fun onNavigateToNested(to: HomeNestedScreen) {
        navigator.navigateTo(to)
    }

    fun onPop() {
        navigator.pop()
    }

    NavDisplay(
        modifier = modifier,
        backStack = navigator.backStack,
        entryDecorators =
            listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider =
            entryProvider {
                entry(HomeNestedScreen.Discover) {
                    Discover(
                        onNavigateToNested = ::onNavigateToNested,
                    )
                }
                entry(HomeNestedScreen.Track) {
                    Track()
                }
                entry(HomeNestedScreen.SearchInput) {
                    SearchInput(
                        onPop = ::onPop,
                        onNavigateToNested = ::onNavigateToNested,
                    )
                }
            },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun NavigationArea(
    selected: TopLevelNavigation,
    modifier: Modifier = Modifier,
    onItemClick: (TopLevelNavigation) -> Unit = {},
) {
    FlexibleBottomAppBar(
        modifier = modifier.height(72.dp),
        horizontalArrangement = BottomAppBarDefaults.FlexibleFixedHorizontalArrangement,
        content = {
            TopLevelNavigation.entries.forEach { item ->
                NavigationBarItem(
                    selected = selected == item,
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
        },
    )
}

enum class TopLevelNavigation {
    DISCOVER,
    TRACK,
}

private val TopLevelNavigation.selectedIcon
    get() =
        when (this) {
            TopLevelNavigation.DISCOVER -> Icons.Default.Explore
            TopLevelNavigation.TRACK -> Icons.Default.CollectionsBookmark
        }

private val TopLevelNavigation.unselectedIcon
    get() =
        when (this) {
            TopLevelNavigation.DISCOVER -> Icons.Outlined.Explore
            TopLevelNavigation.TRACK -> Icons.Outlined.CollectionsBookmark
        }

private class NestedNavigator(
    val backStack: SnapshotStateList<HomeNestedScreen>,
) {
    val currentTopLevelNavigation: TopLevelNavigation
        get() =
            backStack
                .map {
                    it.toTopLevelNavigation()
                }.lastOrNull()
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

    fun navigateTo(to: HomeNestedScreen) {
        Napier.d(tag = TAG) { "Navigated to $to before : ${backStack.toList()}" }
        if (backStack.lastOrNull() == to) {
            Napier.d(tag = TAG) { "Already on $to" }
            return
        }

        backStack.add(to)

        Napier.d(tag = TAG) { "Navigated to $to after : ${backStack.toList()}" }
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }

    private fun TopLevelNavigation.toScreen() =
        when (this) {
            TopLevelNavigation.DISCOVER -> HomeNestedScreen.Discover
            TopLevelNavigation.TRACK -> HomeNestedScreen.Track
        }

    fun HomeNestedScreen.toTopLevelNavigation() =
        when (this) {
            HomeNestedScreen.Discover -> TopLevelNavigation.DISCOVER
            HomeNestedScreen.Track -> TopLevelNavigation.TRACK
            else -> null
        }
}
