package me.andannn.aniflow.data.util

import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.internal.toDomainType
import me.andannn.aniflow.data.internal.toEntity
import me.andannn.aniflow.data.internal.toServiceType
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.StringKeyEnum
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.schema.MediaListEntity
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.dto.MediaList
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal interface DataSyncer<T> {
    suspend fun getLocal(): T

    suspend fun saveLocal(data: T)

    /**
     * Sync the local data with remote.
     *
     * @return The updated model from remote.
     */
    suspend fun syncWithRemote(model: T): T
}

internal suspend fun <T> DataSyncer<T>.postMutationAndRevertWhenException(modify: (T) -> T): AppError? {
    val oldModel = getLocal()
    val newModel = modify(oldModel)
    if (oldModel == newModel) return null

    saveLocal(newModel)

    return try {
        val result = syncWithRemote(newModel)
        saveLocal(result)
        null
    } catch (e: Throwable) {
        val error = e.toError()
        saveLocal(oldModel)
        error
    }
}

internal class MediaListModificationSyncer(
    private val mediaListId: String,
) : DataSyncer<MediaListModificationSyncer.Param>,
    KoinComponent {
    private val mediaLibraryDao: MediaLibraryDao by inject()
    private val service: AniListService by inject()

    @OptIn(ExperimentalTime::class)
    override suspend fun getLocal(): Param =
        mediaLibraryDao.getMediaListById(mediaListId)?.toParam()?.copy(
            updatedAt = Clock.System.now().epochSeconds, // Update the updatedAt to current time
        )
            ?: error("MediaList $mediaListId not found")

    override suspend fun saveLocal(data: Param) {
        mediaLibraryDao.updateMediaList(
            mediaListId = mediaListId,
            status = data.mediaListStatus?.key,
            progress = data.progress?.toLong(),
            updateAt = data.updatedAt,
        )
    }

    override suspend fun syncWithRemote(model: Param) =
        service
            .updateMediaList(
                id = mediaListId.toInt(),
                status = model.mediaListStatus?.toServiceType(),
                progress = model.progress,
            ).toParam()

    private fun MediaListEntity.toParam() =
        Param(
            mediaListStatus = listStatus?.let { StringKeyEnum.deserialize(it) },
            progress = progress?.toInt(),
            updatedAt = updatedAt,
        )

    private fun MediaList.toParam() =
        Param(
            mediaListStatus = status?.toDomainType(),
            progress = progress,
            updatedAt = updatedAt?.toLong(),
        )

    data class Param(
        val mediaListStatus: MediaListStatus? = null,
        val progress: Int? = null,
        val updatedAt: Long? = null,
    )
}
