/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.StaffWithRole
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor
import me.andannn.aniflow.data.model.relation.MediaModelWithRelationType
import me.andannn.aniflow.usecase.data.provider.DetailMediaUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DetailUiState
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncDetailMediaTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.SyncMediaListItemOfAuthedUserTask
import me.andannn.aniflow.usecase.data.provider.internal.tasks.createSideEffectFlow

class DetailMediaUiDataProviderImpl(
    override val mediaId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : DetailMediaUiDataProvider {
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun uiDataFlow(): Flow<DetailUiState> {
        val mediaFlow = mediaRepository.getMediaFlow(mediaId)
        val studioListFlow = mediaRepository.getStudioOfMediaFlow(mediaId)
        val staffListFlow = mediaRepository.getStaffOfMediaFlow(mediaId)
        val relationsFlow = mediaRepository.getRelationsOfMediaFlow(mediaId)
        val characterFlow = mediaRepository.getCharactersOfMediaFlow(mediaId, StaffLanguage.JAPANESE)
        val userOptionsFlow = authRepository.getUserOptionsFlow()
        val authedUserFlow = authRepository.getAuthedUserFlow()
        val mediaListItemFlow =
            authRepository.getAuthedUserFlow().flatMapLatest { authedUser ->
                if (authedUser == null) {
                    flow { emit(null) }
                } else {
                    mediaRepository.getMediaListItemOfUserFlow(
                        userId = authedUser.id,
                        mediaId = mediaId,
                    )
                }
            }

        val detailMediaFlow =
            combine(
                mediaFlow,
                studioListFlow,
                staffListFlow,
                relationsFlow,
                mediaListItemFlow,
                characterFlow,
            ) { array ->
                DetailMedia(
                    media = array[0] as MediaModel,
                    studioList = array[1] as List<StudioModel>,
                    staffList = array[2] as List<StaffWithRole>,
                    relations = array[3] as List<MediaModelWithRelationType>,
                    mediaListItem = array[4] as MediaListModel?,
                    characters = array[5] as List<CharacterWithVoiceActor>,
                )
            }

        return combine(
            detailMediaFlow,
            authedUserFlow,
            userOptionsFlow,
        ) { detailMedia, authedUser, userOptions ->
            DetailUiState(
                mediaModel = detailMedia.media,
                mediaListItem = detailMedia.mediaListItem,
                studioList = detailMedia.studioList,
                staffList = detailMedia.staffList,
                relations = detailMedia.relations,
                characters = detailMedia.characters,
                userOptions = userOptions,
                authedUser = authedUser,
            )
        }.distinctUntilChanged()
    }

    override fun uiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus> =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncMediaListItemOfAuthedUserTask(mediaId),
            SyncDetailMediaTask(mediaId),
        )
}

private data class DetailMedia(
    val media: MediaModel?,
    val mediaListItem: MediaListModel?,
    val studioList: List<StudioModel>,
    val staffList: List<StaffWithRole>,
    val relations: List<MediaModelWithRelationType>,
    val characters: List<CharacterWithVoiceActor>,
)
