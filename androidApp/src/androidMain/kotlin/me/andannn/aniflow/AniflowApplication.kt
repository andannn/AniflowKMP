/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow

import android.app.Application
import android.content.Context
import me.andannn.aniflow.data.KoinHelper
import me.andannn.aniflow.data.KoinHelper.Modules
import me.andannn.aniflow.data.Logger
import me.andannn.aniflow.platform.BrowserAuthOperationHandlerImpl
import me.andannn.aniflow.platform.NetworkConnectivityImpl
import me.andannn.aniflow.platform.PresentationDummyHandler
import me.andannn.aniflow.ui.DetailCharacterViewModel
import me.andannn.aniflow.ui.DetailMediaCharacterPagingViewModel
import me.andannn.aniflow.ui.DetailMediaStaffPagingViewModel
import me.andannn.aniflow.ui.DetailMediaViewModel
import me.andannn.aniflow.ui.DetailStaffViewModel
import me.andannn.aniflow.ui.DiscoverViewModel
import me.andannn.aniflow.ui.HomeViewModel
import me.andannn.aniflow.ui.LoginDialogViewModel
import me.andannn.aniflow.ui.MediaCategoryPagingViewModel
import me.andannn.aniflow.ui.NotificationViewModel
import me.andannn.aniflow.ui.ScoringDialogViewModel
import me.andannn.aniflow.ui.SearchViewModel
import me.andannn.aniflow.ui.SettingOptionViewModel
import me.andannn.aniflow.ui.SettingsViewModel
import me.andannn.aniflow.ui.TrackProgressDialogViewModel
import me.andannn.aniflow.ui.TrackViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AniflowApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Logger.enableDebugLog()
        }

        KoinHelper.startKoin(
            modules =
                listOf(
                    *Modules.toTypedArray(),
                    androidContextModule(this@AniflowApplication),
                ),
            browserAuthOperationHandler =
                if (isPresentationMode()) {
                    PresentationDummyHandler
                } else {
                    BrowserAuthOperationHandlerImpl()
                },
            networkConnectivity = NetworkConnectivityImpl(this@AniflowApplication),
        )
    }
}

fun isPresentationMode() = BuildConfig.PRESENTATION_TOKEN.isNotEmpty()

private fun androidContextModule(application: AniflowApplication) =
    module {
        single { application } bind Context::class
        viewModelOf(::DiscoverViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::NotificationViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::TrackViewModel)
        viewModelOf(::LoginDialogViewModel)
        viewModel {
            DetailMediaViewModel(it.get(), get(parameters = { it }), get(), get())
        }
        viewModel {
            DetailMediaStaffPagingViewModel(it.get(), get(parameters = { it }))
        }
        viewModel {
            DetailMediaCharacterPagingViewModel(it.get(), get(parameters = { it }))
        }
        viewModel {
            MediaCategoryPagingViewModel(it.get(), get())
        }
        viewModel {
            ScoringDialogViewModel(it.get(), get(), get())
        }
        viewModel {
            SettingOptionViewModel(it.get())
        }
        viewModel {
            TrackProgressDialogViewModel(it.get(), get(), get())
        }
        viewModel {
            DetailStaffViewModel(it.get(), get(parameters = { it }))
        }
        viewModel {
            DetailCharacterViewModel(it.get(), get(parameters = { it }))
        }
        viewModelOf(::SearchViewModel)
    }
