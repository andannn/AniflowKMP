/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow

import android.app.Application
import android.content.Context
import me.andannn.aniflow.components.KoinLauncher
import me.andannn.aniflow.components.Logger
import me.andannn.aniflow.components.Modules
import org.koin.dsl.bind
import org.koin.dsl.module

class AniflowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Logger.enableDebugLog()
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
