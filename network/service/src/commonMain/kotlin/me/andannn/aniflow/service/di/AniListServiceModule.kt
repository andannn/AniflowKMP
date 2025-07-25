package me.andannn.aniflow.service.di

import me.andannn.aniflow.service.AniListService
import org.koin.dsl.module

val serviceModule = module {
    single { AniListService() }
}