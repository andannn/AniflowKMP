/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow

import android.app.Application
import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.andannn.aniflow.components.KoinLauncher
import me.andannn.aniflow.components.Modules
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AniflowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }

        KoinLauncher.startKoin(
            modules =
                listOf(
                    *Modules.toTypedArray(),
                    androidContextModule(this@AniflowApplication),
                ),
        )
//        startKoin {
//            androidContext(this@AniflowApplication)
//            modules(dataModule)
//        }
    }
}

private fun androidContextModule(application: AniflowApplication) =
    module {
        single { application } bind Context::class
    }
