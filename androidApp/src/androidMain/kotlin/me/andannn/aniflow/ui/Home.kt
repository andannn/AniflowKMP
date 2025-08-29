/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.HomeAppBarUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.Screen
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.define.MediaContentMode
import org.koin.compose.viewmodel.koinViewModel

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

class HomeViewModel(
    val homeUiDataProvider: HomeAppBarUiDataProvider,
    private val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeAppBarUiState.Empty)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            homeUiDataProvider.appBarFlow().collect { uiState ->
                _state.value = uiState
            }
        }
    }

    fun changeContentMode(mode: MediaContentMode) {
        Napier.d(tag = TAG) { "changeContentMode: $mode" }
        viewModelScope.launch {
            mediaRepository.setContentMode(mode)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(homeViewModel: HomeViewModel = koinViewModel()) {
    val state by homeViewModel.state.collectAsStateWithLifecycle()
    val navigator = LocalRootNavigator.current

    HomeContent(
        state = state,
        navigator =
            remember {
                NestedNavigator(
                    mutableStateListOf(HomeNestedScreen.Discover),
                )
            },
        onContentTypeChange = homeViewModel::changeContentMode,
        onAuthIconClick = {
            navigator.navigateTo(Screen.Dialog.Login)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeAppBarUiState,
    navigator: NestedNavigator,
    onContentTypeChange: (MediaContentMode) -> Unit = {},
    onAuthIconClick: () -> Unit = {},
) {
    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior()
    val bottomBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val user = state.authedUser

    Scaffold(
        modifier =
            modifier
                .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
                .nestedScroll(bottomBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = appBarScrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(),
                title = {
                    Text(
                        text = navigator.currentTopLevelNavigation.label,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    Switch(
                        state.contentMode == MediaContentMode.ANIME,
                        onCheckedChange = { check ->
                            if (check) {
                                onContentTypeChange(MediaContentMode.ANIME)
                            } else {
                                onContentTypeChange(MediaContentMode.MANGA)
                            }
                        },
                    )
                    if (user != null) {
                        BadgedBox(
                            badge = {
                                val badgeNumber = user.unreadNotificationCount
                                if (badgeNumber != 0) {
                                    Badge {
                                        Text(
                                            badgeNumber.toString(),
                                        )
                                    }
                                }
                            },
                        ) {
                            IconButton(
                                onClick = onAuthIconClick,
                            ) {
                                AsyncImage(
                                    model = user.avatar,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    } else {
                        IconButton(
                            onClick = onAuthIconClick,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
            )
        },
        bottomBar = {
            NavigationArea(
                scrollBehavior = bottomBarScrollBehavior,
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
                        .padding(paddingValues)
                        .fillMaxSize(),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun NavigationArea(
    scrollBehavior: BottomAppBarScrollBehavior,
    selected: TopLevelNavigation,
    modifier: Modifier = Modifier,
    onItemClick: (TopLevelNavigation) -> Unit = {},
) {
    FlexibleBottomAppBar(
        modifier = modifier,
        horizontalArrangement = BottomAppBarDefaults.FlexibleFixedHorizontalArrangement,
        scrollBehavior = scrollBehavior,
        content = {
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
        },
    )
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
