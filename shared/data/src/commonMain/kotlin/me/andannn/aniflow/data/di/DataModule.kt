/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.di

import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DetailCharacterUiDataProvider
import me.andannn.aniflow.data.DetailMediaUiDataProvider
import me.andannn.aniflow.data.DetailStaffUiDataProvider
import me.andannn.aniflow.data.DiscoverUiDataProvider
import me.andannn.aniflow.data.HomeAppBarUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SettingUiDataProvider
import me.andannn.aniflow.data.TrackUiDataProvider
import me.andannn.aniflow.data.internal.AuthRepositoryImpl
import me.andannn.aniflow.data.internal.MediaRepositoryImpl
import me.andannn.aniflow.data.internal.dataprovider.DataProviderImpl
import me.andannn.aniflow.data.internal.dataprovider.DetailCharacterUiDataProviderImpl
import me.andannn.aniflow.data.internal.dataprovider.DetailMediaUiDataProviderImpl
import me.andannn.aniflow.data.internal.dataprovider.DetailStaffUiDataProviderImpl
import me.andannn.aniflow.data.model.DetailCharacterUiState
import me.andannn.aniflow.database.di.databaseModule
import me.andannn.aniflow.datastore.di.userPreferencesModule
import me.andannn.aniflow.service.di.serviceModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule =
    module {
        singleOf(::MediaRepositoryImpl).bind(MediaRepository::class)
        singleOf(::AuthRepositoryImpl).bind(AuthRepository::class)
        singleOf(::DataProviderImpl).bind(DiscoverUiDataProvider::class)
        singleOf(::DataProviderImpl).bind(HomeAppBarUiDataProvider::class)
        singleOf(::DataProviderImpl).bind(SettingUiDataProvider::class)
        singleOf(::DataProviderImpl).bind(TrackUiDataProvider::class)
        factory { (mediaId: String) -> DetailMediaUiDataProviderImpl(mediaId, get(), get()) }.bind(
            DetailMediaUiDataProvider::class,
        )
        factory { (staffId: String) -> DetailStaffUiDataProviderImpl(staffId, get(), get()) }.bind(
            DetailStaffUiDataProvider::class,
        )
        factory { (characterId: String) -> DetailCharacterUiDataProviderImpl(characterId, get(), get()) }.bind(
            DetailCharacterUiDataProvider::class,
        )
        includes(
            databaseModule,
            serviceModule,
            userPreferencesModule,
        )
    }
