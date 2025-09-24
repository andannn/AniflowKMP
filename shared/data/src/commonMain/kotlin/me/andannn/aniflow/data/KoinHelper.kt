/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import me.andannn.aniflow.data.di.dataModule
import me.andannn.aniflow.data.internal.dataprovider.DetailStaffUiDataProviderImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
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
        networkConnectivity: NetworkConnectivity,
    ) {
        startKoin {
            modules(modules + platformModule(browserAuthOperationHandler, networkConnectivity))
        }
    }

    // interop with swift.
    fun koinInstance() = getKoin()

    // interop with swift.
    fun trackDataProvider() = koinInstance().get<TrackUiDataProvider>()

    // interop with swift.
    fun discoverDataProvider() = koinInstance().get<DiscoverUiDataProvider>()

    // interop with swift.
    fun homeAppBarUiDataProvider() = koinInstance().get<HomeAppBarUiDataProvider>()

    fun settingUiDataProvider() = koinInstance().get<SettingUiDataProvider>()

    fun detailCharacterUiDataProvider(characterId: String) =
        koinInstance().get<DetailCharacterUiDataProvider>(
            parameters = { parametersOf(characterId) },
        )

    fun detailStaffUiDataProvider(staffId: String) =
        koinInstance().get<DetailStaffUiDataProvider>(
            parameters = { parametersOf(staffId) },
        )

    fun detailMediaUiDataProvider(mediaId: String) =
        koinInstance().get<DetailMediaUiDataProvider>(
            parameters = { parametersOf(mediaId) },
        )

    // interop with swift.
    fun mediaRepository() = koinInstance().get<MediaRepository>()

    // interop with swift.
    fun authRepository() = koinInstance().get<AuthRepository>()

    // interop with swift.
    fun notificationFetchTask() = FetchNotificationTask()

    private fun platformModule(
        browserAuthOperationHandler: BrowserAuthOperationHandler,
        networkConnectivity: NetworkConnectivity,
    ) = module {
        single<BrowserAuthOperationHandler> { browserAuthOperationHandler }
        single<NetworkConnectivity> { networkConnectivity }
    }
}
