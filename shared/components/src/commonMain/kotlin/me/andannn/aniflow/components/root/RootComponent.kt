package me.andannn.aniflow.components.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import me.andannn.aniflow.components.home.HomeComponent

interface RootComponent : BackHandlerOwner {
    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed interface Child {
        class Home(
            val component: HomeComponent,
        ) : Child
    }
}
