package me.andannn.aniflow.data.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.coroutineScope
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.database.MediaLibraryDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncDetailMediaTask(
    private val mediaItemId: String,
) : SideEffectTask<SyncStatus>,
    KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val mediaLibraryDao: MediaLibraryDao by inject()

    override suspend fun WrappedProducerScope<SyncStatus>.register(forceRefresh: Boolean) =
        with(mediaLibraryDao) {
            Napier.d(tag = TAG) { "SyncMediaListItemTask run" }

            val key = TaskRefreshKey.SyncDetailMediaItem(mediaItemId)
            doRefreshIfNeeded2(
                key,
                forceRefresh,
            ) {
                coroutineScope {
                    mediaRepo.syncDetailMedia(this, mediaItemId).await()?.toError()
                }
            }
        }
}
