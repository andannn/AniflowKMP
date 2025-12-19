/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.StaffWithRole
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.getUserTitleString
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor
import me.andannn.aniflow.data.model.relation.MediaModelWithRelationType

data class DetailUiState(
    val mediaModel: MediaModel?,
    val mediaListItem: MediaListModel? = null,
    val studioList: List<StudioModel> = emptyList(),
    val staffList: List<StaffWithRole> = emptyList(),
    val characters: List<CharacterWithVoiceActor> = emptyList(),
    val relations: List<MediaModelWithRelationType> = emptyList(),
    val userOptions: UserOptions = UserOptions.Default,
    val authedUser: UserModel? = null,
) {
    val title: String
        get() = mediaModel?.title?.getUserTitleString(userOptions.titleLanguage) ?: ""
    val bottomBarStatus: BottomBarState
        get() =
            when {
                authedUser == null -> BottomBarState.NEED_LOGIN
                mediaListItem == null -> BottomBarState.AUTHED_WITHOUT_LIST_ITEM
                else -> BottomBarState.AUTHED_WITH_LIST_ITEM
            }

    val mediaListOptions: List<MediaListStatus> =
        run {
            val status = mediaModel?.status
            val allOptions = MediaListStatus.entries
            when (status) {
                MediaStatus.NOT_YET_RELEASED -> {
                    listOf(
                        MediaListStatus.PLANNING,
                        MediaListStatus.DROPPED,
                    )
                }

                MediaStatus.RELEASING -> {
                    listOf(
                        MediaListStatus.CURRENT,
                        MediaListStatus.PLANNING,
                        MediaListStatus.DROPPED,
                        MediaListStatus.PAUSED,
                    )
                }

                MediaStatus.FINISHED,
                MediaStatus.CANCELLED,
                MediaStatus.HIATUS,
                null,
                -> {
                    allOptions
                }
            }
        }

    companion object {
        val Empty =
            DetailUiState(
                mediaModel = null,
            )
    }
}

interface DetailMediaUiDataProvider : DataProvider {
    val mediaId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailUiState>
}
