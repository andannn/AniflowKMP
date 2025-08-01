package me.andannn.aniflow.components

import me.andannn.aniflow.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

val Modules = listOf(
    dataModule
)

object KoinLauncher {
    fun startKoin(
        modules: List<Module>,
    ) {
        startKoin {
            modules(modules)
        }
    }
}
