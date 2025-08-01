package me.andannn.aniflow.components.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import me.andannn.aniflow.components.discover.DefaultDiscoverComponent

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

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): HomeComponent.Child =
        when (config) {
            Config.Discover ->
                HomeComponent.Child.Discover(DefaultDiscoverComponent(componentContext))
        }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Discover : Config
    }
}
