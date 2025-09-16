package me.andannn.aniflow.data.internal.dataprovider

import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.DetailMediaUiDataProvider
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.tasks.SyncDetailMediaTask
import me.andannn.aniflow.data.internal.tasks.SyncMediaListItemOfAuthedUserTask
import me.andannn.aniflow.data.internal.tasks.createSideEffectFlow
import me.andannn.aniflow.data.model.DetailUiState

class DetailMediaUiDataProviderImpl(
    override val mediaId: String,
) : DetailMediaUiDataProvider {
    override fun detailUiDataFlow(): Flow<DetailUiState> {
        TODO("Not yet implemented")
    }

    override fun detailUiSideEffect(forceRefreshFirstTime: Boolean): Flow<SyncStatus> =
        createSideEffectFlow(
            forceRefreshFirstTime,
            SyncMediaListItemOfAuthedUserTask(mediaId),
            SyncDetailMediaTask(mediaId),
        )
}
