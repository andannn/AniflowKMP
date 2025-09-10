/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow

import android.app.Application
import android.content.Context
import me.andannn.aniflow.data.util.KoinHelper
import me.andannn.aniflow.data.util.KoinHelper.Modules
import me.andannn.aniflow.data.util.Logger
import me.andannn.aniflow.platform.BrowserAuthOperationHandlerImpl
import me.andannn.aniflow.ui.DiscoverViewModel
import me.andannn.aniflow.ui.HomeViewModel
import me.andannn.aniflow.ui.LoginDialogViewModel
import me.andannn.aniflow.ui.MediaCategoryPagingViewModel
import me.andannn.aniflow.ui.NotificationViewModel
import me.andannn.aniflow.ui.SearchViewModel
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
            BrowserAuthOperationHandlerImpl(),
        )
    }
}

private fun androidContextModule(application: AniflowApplication) =
    module {
        single { application } bind Context::class
        viewModelOf(::DiscoverViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::NotificationViewModel)
        viewModelOf(::TrackViewModel)
        viewModelOf(::LoginDialogViewModel)
        viewModel {
            MediaCategoryPagingViewModel(it.get(), get())
        }
        viewModelOf(::SearchViewModel)
    }
