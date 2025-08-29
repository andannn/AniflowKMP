/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.util

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.data.background.FetchNotificationTask
import me.andannn.aniflow.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

object KoinHelper {
    val Modules =
        listOf(
            dataModule,
        )

    fun startKoin(
        modules: List<Module>,
        browserAuthOperationHandler: BrowserAuthOperationHandler,
    ) {
        startKoin {
            modules(modules + platformModule(browserAuthOperationHandler))
        }
    }

    fun koinInstance() = getKoin()

    fun trackDataProvider() = koinInstance().get<me.andannn.aniflow.data.TrackUiDataProvider>()

    fun discoverDataProvider() = koinInstance().get<me.andannn.aniflow.data.DiscoverUiDataProvider>()

    fun homeAppBarUiDataProvider() = koinInstance().get<me.andannn.aniflow.data.HomeAppBarUiDataProvider>()

    fun mediaRepository() = koinInstance().get<me.andannn.aniflow.data.MediaRepository>()

    fun authRepository() = koinInstance().get<me.andannn.aniflow.data.AuthRepository>()

    fun notificationFetchTask() = FetchNotificationTask()

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
