/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val Modules =
    listOf(
        dataModule,
    )

object KoinLauncher {
    fun startKoin(
        modules: List<Module>,
        browserAuthOperationHandler: BrowserAuthOperationHandler,
    ) {
        startKoin {
            modules(modules + platformModule(browserAuthOperationHandler))
        }
    }

    private fun platformModule(browserAuthOperationHandler: BrowserAuthOperationHandler) =
        module {
            single<BrowserAuthOperationHandler> { browserAuthOperationHandler }
        }
}

object Logger {
    fun enableDebugLog() {
        Napier.base(DebugAntilog())
    }
}
