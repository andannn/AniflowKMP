/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import me.andannn.aniflow.platform.NetworkConnectivity
import me.andannn.aniflow.platform.PlatformAnalytics
import me.andannn.aniflow.usecase.data.provider.dataProviderImplModule
import org.koin.core.module.Module
import org.koin.dsl.module

object KoinHelper {
    val Modules =
        listOf(
            domainImplModule,
            dataProviderImplModule,
        )

    fun startKoin(
        modules: List<Module>,
        browserAuthOperationHandler: BrowserAuthOperationHandler,
        networkConnectivity: NetworkConnectivity,
        platformAnalytics: PlatformAnalytics,
    ) {
        org.koin.core.context.startKoin {
            modules(
                modules +
                    platformModule(
                        browserAuthOperationHandler,
                        networkConnectivity,
                        platformAnalytics,
                    ),
            )
        }
    }

    private fun platformModule(
        browserAuthOperationHandler: BrowserAuthOperationHandler,
        networkConnectivity: NetworkConnectivity,
        platformAnalytics: PlatformAnalytics,
    ) = module {
        single<BrowserAuthOperationHandler> { browserAuthOperationHandler }
        single<NetworkConnectivity> { networkConnectivity }
        single<PlatformAnalytics> { platformAnalytics }
    }
}
