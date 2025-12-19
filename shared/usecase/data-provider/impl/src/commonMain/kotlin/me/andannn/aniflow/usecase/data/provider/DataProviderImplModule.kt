/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import me.andannn.aniflow.usecase.data.provider.internal.DetailCharacterUiDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.DetailMediaUiDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.DetailStaffUiDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.DetailStudioUiDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.DiscoverUiDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.HomeAppBarUiDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.SettingDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.TrackProgressDialogDataProviderImpl
import me.andannn.aniflow.usecase.data.provider.internal.TrackUiDataProviderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataProviderImplModule =
    module {
        singleOf(::DiscoverUiDataProviderImpl).bind(DiscoverUiDataProvider::class)
        singleOf(::HomeAppBarUiDataProviderImpl).bind(HomeAppBarUiDataProvider::class)
        singleOf(::SettingDataProviderImpl).bind(SettingUiDataProvider::class)
        singleOf(::TrackUiDataProviderImpl).bind(TrackUiDataProvider::class)
        factory { (mediaId: String) -> DetailMediaUiDataProviderImpl(mediaId, get(), get()) }.bind(
            DetailMediaUiDataProvider::class,
        )
        factory { (mediaId: String) ->
            TrackProgressDialogDataProviderImpl(
                mediaId,
                get(),
                get(),
            )
        }.bind(
            TrackProgressDialogDataProvider::class,
        )
        factory { (staffId: String) -> DetailStaffUiDataProviderImpl(staffId, get(), get()) }.bind(
            DetailStaffUiDataProvider::class,
        )
        factory { (studioId: String) ->
            DetailStudioUiDataProviderImpl(
                studioId,
                get(),
                get(),
            )
        }.bind(
            DetailStudioUiDataProvider::class,
        )

        factory { (characterId: String) ->
            DetailCharacterUiDataProviderImpl(
                characterId,
                get(),
                get(),
            )
        }.bind(
            DetailCharacterUiDataProvider::class,
        )
    }
