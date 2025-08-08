/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import me.andannn.aniflow.components.common.paging.DefaultMediaCategoryPageComponent
import me.andannn.aniflow.components.home.DefaultHomeComponent
import me.andannn.aniflow.data.model.define.MediaCategory

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
        nav.pop()
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            is Config.Home -> {
                RootComponent.Child.Home(
                    DefaultHomeComponent(
                        componentContext = componentContext,
                        onNavigateToMediaCategoryPage = { mediaCategory ->
                            nav.pushNew(Config.MediaCategoryPage(mediaCategory))
                        },
                    ),
                )
            }

            is Config.MediaCategoryPage ->
                RootComponent.Child.MediaCategoryPage(
                    DefaultMediaCategoryPageComponent(
                        componentContext = componentContext,
                        category = config.mediaCategory,
                    ),
                )
        }

    @Serializable
    internal sealed interface Config {
        @Serializable
        data object Home : Config

        @Serializable
        data class MediaCategoryPage(
            val mediaCategory: MediaCategory,
        ) : Config
    }
}
