/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.util

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.internal.toDomainType
import me.andannn.aniflow.data.internal.toEntity
import me.andannn.aniflow.data.internal.toServiceType
import me.andannn.aniflow.data.model.StaffCharacterName
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.StringKeyEnum
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.schema.MediaListEntity
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

internal suspend fun <T> DataSyncer<T>.postMutationAndRevertWhenException(modify: (T) -> T): AppError? {
    val oldModel = getLocal()
    val newModel = modify(oldModel)
    if (oldModel == newModel) {
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

internal class UserSettingSyncer :
    DataSyncer<UserSettingSyncer.Param>,
    KoinComponent {
    private val service: AniListService by inject()
    private val preferences: UserSettingPreferences by inject()

    override suspend fun getLocal(): Param =
        preferences.userData.first().let {
            Param(
                userTitleLanguage = it.titleLanguage?.let { StringKeyEnum.deserialize(it) },
                userStaffNameLanguage = it.staffNameLanguage?.let { StringKeyEnum.deserialize(it) },
                appTheme = it.appTheme?.let { StringKeyEnum.deserialize(it) },
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
                ).toParam()
        } else {
            new
        }

    private fun User?.toParam(): Param =
        Param(
            userTitleLanguage = this?.options?.titleLanguage?.toDomainType(),
            userStaffNameLanguage = this?.options?.staffNameLanguage?.toDomainType(),
        )

    data class Param(
        val userTitleLanguage: UserTitleLanguage? = null,
        val displayAdultContent: Boolean? = null,
        val userStaffNameLanguage: UserStaffNameLanguage? = null,
        val appTheme: Theme? = null,
    ) {
        fun needSync(new: Param): Boolean =
            userTitleLanguage != new.userTitleLanguage ||
                userStaffNameLanguage != new.userStaffNameLanguage
    }
}
