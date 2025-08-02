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
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaType
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

class DefaultDiscoverComponent(
    componentContext: ComponentContext,
    mainContext: CoroutineContext = Dispatchers.Main,
    mediaRepository: MediaRepository = getKoin().get(),
) : DiscoverComponent,
    ComponentContext by componentContext {
    private val scope = coroutineScope(mainContext + SupervisorJob())

    override val categoryDataMap: MutableValue<Map<MediaCategory, List<MediaModel>>> =
        MutableValue(emptyMap())

    private val stateFlow =
        mediaRepository.getAllMediasWithCategory(MediaType.ANIME).map {
            it.data ?: emptyMap()
        }

    init {
        scope.launch {
            stateFlow.collect { newState ->
                categoryDataMap.value = newState
            }
        }
    }
}
