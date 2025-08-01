package me.andannn.aniflow.components.home

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import me.andannn.aniflow.components.discover.DiscoverComponent

interface HomeComponent : BackHandlerOwner {
    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed interface Child {
        class Discover(
            val component: DiscoverComponent,
        ) : Child
    }
}
