/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.home

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import me.andannn.aniflow.components.discover.DiscoverComponent
import me.andannn.aniflow.components.track.TrackComponent

interface HomeComponent : BackHandlerOwner {
    val stack: Value<ChildStack<*, Child>>

    val selectedNavigationItem: Value<TopLevelNavigation>

    fun onBackClicked()

    fun onSelectNavigationItem(navigationItem: TopLevelNavigation)

    sealed interface Child {
        class Discover(
            val component: DiscoverComponent,
        ) : Child

        class Track(
            val component: TrackComponent,
        ) : Child
    }
}

enum class TopLevelNavigation {
    DISCOVER,
    TRACK,
    SOCIAL,
    PROFILE,
}
