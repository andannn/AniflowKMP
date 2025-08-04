/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaType
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

class DefaultDiscoverComponent(
    componentContext: ComponentContext,
    mainContext: CoroutineContext = Dispatchers.Main,
    mediaRepository: MediaRepository = getKoin().get(),
    private val authRepository: AuthRepository = getKoin().get(),
) : DiscoverComponent,
    ComponentContext by componentContext {
    private val scope = coroutineScope(mainContext + SupervisorJob())

    override val categoryDataMap: MutableValue<CategoryDataModel> =
        MutableValue(CategoryDataModel())

    override val authedUser: MutableValue<Optional<UserModel>> =
        MutableValue(Optional(null))

    override fun onStartLoginProcess() {
        scope.launch {
            authRepository.startLoginProcessAndWaitResult()
        }
    }

    init {
        scope.launch {
            mediaRepository
                .getAllMediasWithCategory(MediaType.ANIME)
                .map {
                    it.data ?: emptyMap()
                }.collect { newState ->
                    categoryDataMap.value =
                        CategoryDataModel(
                            newState,
                        )
                }
        }

        scope.launch {
            authRepository.getAuthedUser().collect {
                authedUser.value = Optional(it)
            }
        }
    }
}
