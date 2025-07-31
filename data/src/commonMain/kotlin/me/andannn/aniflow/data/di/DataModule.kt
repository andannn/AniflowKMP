package me.andannn.aniflow.data.di

import me.andannn.aniflow.database.di.databaseModule
import me.andannn.aniflow.service.di.serviceModule
import org.koin.dsl.module

val dataModule =
    module {
        includes(
            databaseModule,
            serviceModule,
        )
    }
