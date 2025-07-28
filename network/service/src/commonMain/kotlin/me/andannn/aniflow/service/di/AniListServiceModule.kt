package me.andannn.aniflow.service.di

import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.token.DataStoreTokenProvider
import org.koin.dsl.module

val serviceModule =
    module {
        single { AniListService(tokenProvider = DataStoreTokenProvider()) }
    }
