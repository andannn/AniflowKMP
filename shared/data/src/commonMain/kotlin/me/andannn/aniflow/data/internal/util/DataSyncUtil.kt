/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.util

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.internal.toDomainType
import me.andannn.aniflow.data.internal.toEntity
import me.andannn.aniflow.data.internal.toServiceType
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.define.deserialize
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.schema.CharacterEntity
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.MediaListEntity
import me.andannn.aniflow.database.schema.StaffEntity
import me.andannn.aniflow.database.schema.StudioEntity
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val TAG = "DataSyncer"

internal interface DataSyncer<T> {
    suspend fun getLocal(): T

    suspend fun saveLocal(data: T)

    /**
     * Sync the local data with remote.
     *
     * @return The updated model from remote.
     */
    suspend fun syncWithRemote(
        old: T,
        new: T,
    ): T
}

internal suspend fun <T> DataSyncer<T>.postMutationAndRevertWhenException(
    syncWhenItemChanged: Boolean = true,
    modify: (T) -> T = { it },
): AppError? {
    val oldModel = getLocal()
    val newModel = modify(oldModel)
    if (syncWhenItemChanged && oldModel == newModel) {
        Napier.d(tag = TAG) { "postMutationAndRevertWhenException same item, just skip" }
        return null
    }

    saveLocal(newModel)

    return try {
        val result = syncWithRemote(oldModel, newModel)
        saveLocal(result)
        Napier.d(tag = TAG) { "postMutationAndRevertWhenException success" }
        null
    } catch (e: Throwable) {
        val error = e.toError()
        saveLocal(oldModel)
        Napier.e(tag = TAG) { "postMutationAndRevertWhenException failed $e" }
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
            score = data.score?.toDouble(),
        )
    }

    override suspend fun syncWithRemote(
        old: Param,
        new: Param,
    ) = service
        .updateMediaList(
            id = mediaListId.toInt(),
            status = new.mediaListStatus?.toServiceType(),
            progress = new.progress,
            score = new.score,
        ).toParam()

    private fun MediaListEntity.toParam() =
        Param(
            mediaListStatus = listStatus?.deserialize(),
            progress = progress?.toInt(),
            updatedAt = updatedAt,
            score = score?.toFloat(),
        )

    private fun MediaList.toParam() =
        Param(
            mediaListStatus = status?.toDomainType(),
            progress = progress,
            updatedAt = updatedAt?.toLong(),
            score = score?.toFloat(),
        )

    data class Param(
        val mediaListStatus: MediaListStatus? = null,
        val progress: Int? = null,
        val updatedAt: Long? = null,
        val score: Float? = null,
    )
}

internal class UserSettingSyncer :
    DataSyncer<UserSettingSyncer.Param>,
    KoinComponent {
    private val service: AniListService by inject()
    private val preferences: UserSettingPreferences by inject()

    override suspend fun getLocal(): Param =
        preferences.userData.first().let {
            Param(
                userTitleLanguage = it.titleLanguage?.deserialize(),
                userStaffNameLanguage = it.staffNameLanguage?.deserialize(),
                appTheme = it.appTheme?.deserialize(),
                scoreFormat = it.scoreFormat?.deserialize(),
            )
        }

    override suspend fun saveLocal(data: Param) {
        if (data.userTitleLanguage != null) {
            preferences.setTitleLanguage(data.userTitleLanguage.key)
        }
        if (data.userStaffNameLanguage != null) {
            preferences.setStaffNameLanguage(data.userStaffNameLanguage.key)
        }
        if (data.appTheme != null) {
            preferences.setAppTheme(data.appTheme.key)
        }
        if (data.scoreFormat != null) {
            preferences.setScoreFormat(data.scoreFormat.key)
        }
    }

    override suspend fun syncWithRemote(
        old: Param,
        new: Param,
    ): Param =
        if (old.needSync(new)) {
            service
                .updateUserSetting(
                    titleLanguage = new.userTitleLanguage?.toServiceType(),
                    userStaffNameLanguage = new.userStaffNameLanguage?.toServiceType(),
                    scoreFormat = new.scoreFormat?.toServiceType(),
                ).toParam()
        } else {
            new
        }

    private fun User?.toParam(): Param =
        Param(
            userTitleLanguage = this?.options?.titleLanguage?.toDomainType(),
            userStaffNameLanguage = this?.options?.staffNameLanguage?.toDomainType(),
            scoreFormat = this?.mediaListOptions?.scoreFormat?.toDomainType(),
        )

    data class Param(
        val userTitleLanguage: UserTitleLanguage? = null,
        val displayAdultContent: Boolean? = null,
        val userStaffNameLanguage: UserStaffNameLanguage? = null,
        val appTheme: Theme? = null,
        val scoreFormat: ScoreFormat? = null,
    ) {
        fun needSync(new: Param): Boolean =
            userTitleLanguage != new.userTitleLanguage ||
                userStaffNameLanguage != new.userStaffNameLanguage ||
                scoreFormat != new.scoreFormat
    }
}

internal class AddNewListItemSyncer(
    private val mediaId: String,
) : DataSyncer<MediaListEntity?>,
    KoinComponent {
    private val mediaLibraryDao: MediaLibraryDao by inject()
    private val service: AniListService by inject()

    override suspend fun getLocal(): MediaListEntity? {
        // Just return null, as there is no existing item when adding a new one.
        return null
    }

    override suspend fun saveLocal(data: MediaListEntity?) {
        if (data != null) {
            mediaLibraryDao.upsertMediaListEntity(data)
        }
    }

    override suspend fun syncWithRemote(
        old: MediaListEntity?,
        new: MediaListEntity?,
    ): MediaListEntity? =
        service
            .updateMediaList(
                mediaId = mediaId.toInt(),
                status = MediaListStatus.PLANNING.toServiceType(),
                progress = 0,
            ).toEntity(mediaId)
}

internal class ToggleMediaLikeSyncer(
    private val mediaId: String,
    private val mediaType: MediaType,
) : DataSyncer<MediaEntity>,
    KoinComponent {
    private val mediaLibraryDao: MediaLibraryDao by inject()
    private val service: AniListService by inject()

    override suspend fun getLocal(): MediaEntity = mediaLibraryDao.getMediaById(mediaId) ?: error("No media found with id $mediaId")

    override suspend fun saveLocal(data: MediaEntity) {
        mediaLibraryDao.upsertMedias(listOf(data))
    }

    override suspend fun syncWithRemote(
        old: MediaEntity,
        new: MediaEntity,
    ): MediaEntity {
        if (mediaType == MediaType.ANIME) {
            service.toggleFavorite(
                animeId = mediaId.toInt(),
            )
        } else {
            service.toggleFavorite(
                mangaId = mediaId.toInt(),
            )
        }
        return service.getDetailMedia(mediaId.toInt()).media.toEntity()
    }
}

internal class ToggleStaffLikeSyncer(
    private val staffId: String,
) : DataSyncer<StaffEntity>,
    KoinComponent {
    private val mediaLibraryDao: MediaLibraryDao by inject()
    private val service: AniListService by inject()

    override suspend fun getLocal(): StaffEntity = mediaLibraryDao.getStaffById(staffId) ?: error("No staff found with id $staffId")

    override suspend fun saveLocal(data: StaffEntity) {
        mediaLibraryDao.upsertStaff(data)
    }

    override suspend fun syncWithRemote(
        old: StaffEntity,
        new: StaffEntity,
    ): StaffEntity {
        service.toggleFavorite(
            staffId = staffId.toInt(),
        )
        return service.getStaffDetail(staffId.toInt())?.toEntity()
            ?: error("No staff found with id $staffId")
    }
}

internal class ToggleStudioLikeSyncer(
    private val studioId: String,
) : DataSyncer<StudioEntity>,
    KoinComponent {
    private val mediaLibraryDao: MediaLibraryDao by inject()
    private val service: AniListService by inject()

    override suspend fun getLocal(): StudioEntity = mediaLibraryDao.getStudioById(studioId) ?: error("No staff found with id $studioId")

    override suspend fun saveLocal(data: StudioEntity) {
        mediaLibraryDao.upsertStudio(data)
    }

    override suspend fun syncWithRemote(
        old: StudioEntity,
        new: StudioEntity,
    ): StudioEntity {
        service.toggleFavorite(
            studioId = studioId.toInt(),
        )
        return service.getStudioDetail(studioId.toInt())?.toEntity()
            ?: error("No staff found with id $studioId")
    }
}

internal class ToggleCharacterLikeSyncer(
    private val characterId: String,
) : DataSyncer<CharacterEntity>,
    KoinComponent {
    private val mediaLibraryDao: MediaLibraryDao by inject()
    private val service: AniListService by inject()

    override suspend fun getLocal(): CharacterEntity =
        mediaLibraryDao.getCharacterById(characterId)
            ?: error("No staff found with id $characterId")

    override suspend fun saveLocal(data: CharacterEntity) {
        mediaLibraryDao.upsertCharacter(data)
    }

    override suspend fun syncWithRemote(
        old: CharacterEntity,
        new: CharacterEntity,
    ): CharacterEntity {
        service.toggleFavorite(
            characterId = characterId.toInt(),
        )
        return service.getCharacterDetail(characterId.toInt())?.toEntity()
            ?: error("No staff found with id $characterId")
    }
}
