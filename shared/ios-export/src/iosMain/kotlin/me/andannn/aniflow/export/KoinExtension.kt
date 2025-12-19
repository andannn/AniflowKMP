/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.export

import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.usecase.data.provider.DetailCharacterUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DetailMediaUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DetailStaffUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DiscoverUiDataProvider
import me.andannn.aniflow.usecase.data.provider.HomeAppBarUiDataProvider
import me.andannn.aniflow.usecase.data.provider.SettingUiDataProvider
import me.andannn.aniflow.usecase.data.provider.TrackProgressDialogDataProvider
import me.andannn.aniflow.usecase.data.provider.TrackUiDataProvider
import me.andannn.aniflow.usecase.data.sync.FetchNotificationTask
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

object KoinExtension {
    // interop with swift.
    fun koinInstance() = getKoin()

    // interop with swift.
    fun trackDataProvider() = koinInstance().get<TrackUiDataProvider>()

    // interop with swift.
    fun discoverDataProvider() = koinInstance().get<DiscoverUiDataProvider>()

    // interop with swift.
    fun homeAppBarUiDataProvider() = koinInstance().get<HomeAppBarUiDataProvider>()

    fun settingUiDataProvider() = koinInstance().get<SettingUiDataProvider>()

    fun trackProgressDialogDataProvider(mediaId: String) =
        koinInstance().get<TrackProgressDialogDataProvider>(
            parameters = { parametersOf(mediaId) },
        )

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
}
