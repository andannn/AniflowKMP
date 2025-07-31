package me.andannn.aniflow.database

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.MediaQueries

class MediaLibraryDao(
    private val aniflowDatabase: AniflowDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun getMediaById(mediaId: String): MediaEntity? =
        withQueries {
            getMedia(mediaId).awaitAsOneOrNull()
        }

    fun getMediaFlow(mediaId: String): Flow<MediaEntity> =
        withQueries {
            getMedia(mediaId).asFlow().mapToOneOrNull(dispatcher).filterNotNull()
        }

    suspend fun getMediasById(mediaIds: List<String>): List<MediaEntity> =
        withQueries {
            getMediasById(mediaIds).awaitAsList()
        }

    suspend fun upsertMedias(mediaList: List<MediaEntity>) =
        withQueries {
            transaction(true) {
                mediaList.forEach { mediaEntity ->
                    upsertMedia(mediaEntity)
                }
            }
        }

    private inline fun <T> withQueries(block: MediaQueries.() -> T): T =
        with(aniflowDatabase) {
            block.invoke(mediaQueries)
        }
}
