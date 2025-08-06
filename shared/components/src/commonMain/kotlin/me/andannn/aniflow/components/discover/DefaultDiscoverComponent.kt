/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.discover

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaType
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

internal class DefaultDiscoverComponent(
    componentContext: ComponentContext,
    mainContext: CoroutineContext = Dispatchers.Main,
    mediaRepository: MediaRepository = getKoin().get(),
    private val authRepository: AuthRepository = getKoin().get(),
) : DiscoverComponent,
    ComponentContext by componentContext {
    private val scope = coroutineScope(mainContext + SupervisorJob())
    private var authJob: Deferred<Unit>? = null

    override val categoryDataMap: MutableValue<CategoryDataModel> =
        MutableValue(CategoryDataModel())

    override val authedUser: MutableValue<Optional<UserModel>> =
        MutableValue(Optional(null))

    override fun onMediaClick(media: MediaModel) {
        cancelLastAndStartLoginProcess()
    }

    private fun cancelLastAndStartLoginProcess() {
        authJob?.cancel()
        authJob = authRepository.startLoginProcessAndWaitResult(scope)
    }

    init {
        scope.launch {
            mediaRepository
                .getAllMediasWithCategoryFlow(MediaType.ANIME)
                .map { it.data ?: emptyMap() }
                .collect { newState ->
                    categoryDataMap.value = CategoryDataModel(newState)
                }
        }

        scope.launch {
            authRepository.getAuthedUser().collect {
                authedUser.value = Optional(it)
            }
        }
    }
}
