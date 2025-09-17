package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DetailMediaUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.tasks.SyncDetailMediaTask
import me.andannn.aniflow.data.internal.tasks.SyncMediaListItemOfAuthedUserTask
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.DetailUiState

class DetailMediaUiDataProviderImpl(
    override val mediaId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : DetailMediaUiDataProvider {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun detailUiDataFlow(): Flow<DetailUiState> {
        val mediaFlow = mediaRepository.getMediaFlow(mediaId)
        val studioListFlow = mediaRepository.getStudioOfMediaFlow(mediaId)
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
        return combine(
            mediaFlow,
            studioListFlow,
            userOptionsFlow,
            mediaListItemFlow,
            authedUserFlow,
        ) { media, studioList, userOptions, mediaListItem, authedUser ->
            DetailUiState(
                mediaModel = media,
                mediaListItem = mediaListItem,
                studioList = studioList,
                userOptions = userOptions,
                authedUser = authedUser,
            )
        }
    }

    override fun detailUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus> =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncMediaListItemOfAuthedUserTask(mediaId),
            SyncDetailMediaTask(mediaId),
        )
}
