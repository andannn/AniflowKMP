package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
    override fun detailUiDataFlow(): Flow<DetailUiState> {
        val mediaFlow = mediaRepository.getMediaFlow(mediaId)
        val studioListFlow = mediaRepository.getStudioOfMediaFlow(mediaId)

        return combine(
            mediaFlow,
            studioListFlow,
        ) { media, studioList ->
            DetailUiState(
                mediaModel = media,
                mediaListItem = null,
                studioList = studioList,
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
