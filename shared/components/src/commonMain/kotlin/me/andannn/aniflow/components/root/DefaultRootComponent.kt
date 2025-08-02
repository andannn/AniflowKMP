/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import me.andannn.aniflow.components.home.DefaultHomeComponent

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent,
    ComponentContext by componentContext {
    private val nav = StackNavigation<Config>()

    private val _stack =
        childStack(
            source = nav,
            serializer = Config.serializer(),
            initialStack = { listOf(Config.Home) },
            childFactory = ::child,
        )

    override val stack: Value<ChildStack<*, RootComponent.Child>> = _stack

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child {
        when (config) {
            is Config.Home -> {
                return RootComponent.Child.Home(
                    DefaultHomeComponent(
                        componentContext = componentContext,
                    ),
                )
            }
        }
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Home : Config
    }
}
