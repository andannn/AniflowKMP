/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.Page
import me.andannn.aniflow.data.model.SearchSource
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.StaffWithRole
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor
import me.andannn.aniflow.data.model.relation.MediaModelWithRelationType
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

interface MediaRepository {
    fun syncMediaCategory(
        scope: CoroutineScope,
        category: MediaCategory,
    ): Deferred<Throwable?>

    fun getMediasFlow(category: MediaCategory): Flow<CategoryWithContents>

    fun getContentModeFlow(): Flow<MediaContentMode>

    @NativeCoroutines
    suspend fun setContentMode(mode: MediaContentMode)

    fun syncMediaListByUserId(
        scope: CoroutineScope,
        userId: String,
        status: List<MediaListStatus>,
        mediaType: MediaType,
        scoreFormat: ScoreFormat,
    ): Deferred<Throwable?>

    fun syncDetailMedia(
        scope: CoroutineScope,
        mediaId: String,
        voiceActorLanguage: StaffLanguage,
    ): Deferred<Throwable?>

    fun syncMediaListItemOfUser(
        scope: CoroutineScope,
        userId: String,
        mediaId: String,
        scoreFormat: ScoreFormat,
    ): Deferred<Throwable?>

    fun getMediaListFlowByUserId(
        userId: String,
        mediaType: MediaType,
        mediaListStatus: List<MediaListStatus>,
    ): Flow<List<MediaWithMediaListItem>>

    fun getMediaListItemOfUserFlow(
        userId: String,
        mediaId: String,
    ): Flow<MediaListModel?>

    fun getNewReleasedAnimeListFlow(
        userId: String,
        timeSecondLaterThan: Long,
    ): Flow<List<MediaWithMediaListItem>>

    /**
     * Load media page by category from remote.
     */
    suspend fun loadMediaPageByCategory(
        category: MediaCategory,
        page: Int,
        perPage: Int,
    ): Pair<Page<MediaModel>, AppError?>

    /**
     * Load notification page by category from remote.
     */
    suspend fun loadNotificationByPage(
        page: Int,
        perPage: Int,
        category: NotificationCategory,
        resetNotificationCount: Boolean,
    ): Pair<Page<NotificationModel>, AppError?>

    @NativeCoroutines
    suspend fun updateMediaListStatus(
        mediaListId: String,
        status: MediaListStatus? = null,
        progress: Int? = null,
        score: Float? = null,
    ): AppError?

    @NativeCoroutines
    suspend fun addNewMediaToList(mediaId: String): AppError?

    @NativeCoroutines
    suspend fun searchMediaFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Media,
    ): Pair<Page<MediaModel>, AppError?>

    @NativeCoroutines
    suspend fun searchCharacterFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Character,
    ): Pair<Page<CharacterModel>, AppError?>

    @NativeCoroutines
    suspend fun searchStaffFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Staff,
    ): Pair<Page<StaffModel>, AppError?>

    @NativeCoroutines
    suspend fun searchStudioFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Studio,
    ): Pair<Page<StudioModel>, AppError?>

    fun getMediaFlow(mediaId: String): Flow<MediaModel>

    fun getStudioOfMediaFlow(mediaId: String): Flow<List<StudioModel>>

    fun getStaffOfMediaFlow(mediaId: String): Flow<List<StaffWithRole>>

    fun getRelationsOfMediaFlow(mediaId: String): Flow<List<MediaModelWithRelationType>>

    fun getCharactersOfMediaFlow(
        mediaId: String,
        language: StaffLanguage,
    ): Flow<List<CharacterWithVoiceActor>>

    @NativeCoroutines
    suspend fun toggleMediaItemLike(
        mediaId: String,
        mediaType: MediaType,
    ): AppError?

    suspend fun getStaffPageOfMedia(
        mediaId: String,
        page: Int,
        perPage: Int,
    ): Pair<Page<StaffWithRole>, AppError?>

    suspend fun getCharacterPageOfMedia(
        mediaId: String,
        characterStaffLanguage: StaffLanguage,
        page: Int,
        perPage: Int,
    ): Pair<Page<CharacterWithVoiceActor>, AppError?>
}
