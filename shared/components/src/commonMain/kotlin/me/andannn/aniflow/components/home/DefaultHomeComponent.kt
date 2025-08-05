/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable
import me.andannn.aniflow.components.discover.DefaultDiscoverComponent
import me.andannn.aniflow.components.track.DefaultTrackComponent

private const val TAG = "DefaultHomeComponent"

class DefaultHomeComponent(
    componentContext: ComponentContext,
) : HomeComponent,
    ComponentContext by componentContext {
    private val nav = StackNavigation<Config>()
    private val _stack =
        childStack(
            source = nav,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.Discover) },
            childFactory = ::child,
        )
    override val stack: Value<ChildStack<*, HomeComponent.Child>> = _stack
    override val selectedNavigationItem: MutableValue<TopLevelNavigation> =
        MutableValue(TopLevelNavigation.DISCOVER)

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }

    override fun onSelectNavigationItem(navigationItem: TopLevelNavigation) {
        Napier.d(tag = TAG) { "onSelectNavigationItem: $navigationItem" }
        selectedNavigationItem.value = navigationItem

        when (navigationItem) {
            TopLevelNavigation.DISCOVER -> nav.bringToFront(Config.Discover)
            TopLevelNavigation.TRACK -> nav.bringToFront(Config.Track)
            TopLevelNavigation.SOCIAL -> TODO()
            TopLevelNavigation.PROFILE -> TODO()
        }
    }

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): HomeComponent.Child =
        when (config) {
            Config.Discover ->
                HomeComponent.Child.Discover(
                    DefaultDiscoverComponent(
                        componentContext = componentContext,
                    ),
                )

            Config.Track ->
                HomeComponent.Child.Track(
                    DefaultTrackComponent(
                        componentContext = componentContext,
                    ),
                )
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Discover : Config

        @Serializable
        data object Track : Config
    }
}
