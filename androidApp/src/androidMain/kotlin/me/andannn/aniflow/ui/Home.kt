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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import me.andannn.aniflow.components.home.HomeComponent
import me.andannn.aniflow.components.home.TopLevelNavigation

@Composable
fun Home(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    val selectedNavigation by component.selectedNavigationItem.subscribeAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationArea(
                selected = selectedNavigation,
                onItemClick = component::onSelectNavigationItem,
            )
        },
        content = { paddingValues ->
            Surface(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
            ) {
                Children(component, modifier)
            }
        },
    )
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
private fun Children(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    Children(
        stack = component.stack,
        modifier = modifier,
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val child = it.instance) {
                is HomeComponent.Child.Discover ->
                    Discover(
                        component = child.component,
                    )
            }
        }
    }
}

@Composable
fun NavigationArea(
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
