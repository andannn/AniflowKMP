/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.track

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.CoroutineContext

private const val TAG = "DefaultTrackComponent"

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultTrackComponent(
    componentContext: ComponentContext,
    mainContext: CoroutineContext = Dispatchers.Main,
    private val mediaRepo: MediaRepository = getKoin().get(),
    private val authRepo: AuthRepository = getKoin().get(),
) : TrackComponent,
    ComponentContext by componentContext {
    private val scope = coroutineScope(mainContext + SupervisorJob())

    override val content: MutableValue<TrackComponent.Content> =
        MutableValue(TrackComponent.Content(emptyList()))

    init {
        Napier.d(tag = TAG) { "DefaultTrackComponent initialized" }
        scope.launch {
            authRepo
                .getAuthedUser()
                .flatMapLatest { authUser ->
                    if (authUser == null) {
                        // If not authenticated, return an empty flow
                        emptyFlow()
                    } else {
                        mediaRepo.getMediaListFlowByUserId(
                            userId = authUser.id,
                            mediaListStatus =
                                listOf(
                                    MediaListStatus.PLANNING,
                                    MediaListStatus.CURRENT,
                                ),
                            mediaType = MediaType.ANIME,
                        )
                    }
                }.collect { (data, errors) ->
                    Napier.d(tag = TAG) { "data $data" }
                    content.value =
                        TrackComponent.Content(
                            items = data ?: emptyList(),
                        )
                }
        }
    }
}
