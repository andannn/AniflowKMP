/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.internal.exceptions.toError
import me.andannn.aniflow.data.internal.util.AddNewListItemSyncer
import me.andannn.aniflow.data.internal.util.MediaListModificationSyncer
import me.andannn.aniflow.data.internal.util.ToggleCharacterLikeSyncer
import me.andannn.aniflow.data.internal.util.ToggleMediaLikeSyncer
import me.andannn.aniflow.data.internal.util.ToggleStaffLikeSyncer
import me.andannn.aniflow.data.internal.util.postMutationAndRevertWhenException
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
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.data.model.define.deserialize
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor
import me.andannn.aniflow.data.model.relation.MediaModelWithRelationType
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.data.model.relation.VoicedCharacterWithMedia
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.database.relation.MediaEntityWithRelationType
import me.andannn.aniflow.database.relation.MediaListAndMediaRelationWithUpdateLog
import me.andannn.aniflow.database.schema.CharacterEntity
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.StaffEntity
import me.andannn.aniflow.database.schema.StudioEntity
import me.andannn.aniflow.datastore.UserSettingPreferences
import me.andannn.aniflow.service.AniListService
import me.andannn.aniflow.service.ServerException
import me.andannn.aniflow.service.dto.Character
import me.andannn.aniflow.service.dto.CharactersConnection
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaConnection
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.MediaRelations
import me.andannn.aniflow.service.dto.NotificationUnion
import me.andannn.aniflow.service.dto.Staff
import me.andannn.aniflow.service.dto.StaffConnection
import me.andannn.aniflow.service.dto.Studio
import me.andannn.aniflow.service.dto.enums.NotificationType
import me.andannn.aniflow.service.dto.toMediaPage
import me.andannn.aniflow.service.dto.toPage
import kotlin.time.ExperimentalTime

private const val TAG = "MediaRepository"

internal class MediaRepositoryImpl(
    private val mediaLibraryDao: MediaLibraryDao,
    private val mediaService: AniListService,
    private val userPreference: UserSettingPreferences,
) : MediaRepository {
    override fun syncMediaCategory(
        scope: CoroutineScope,
        category: MediaCategory,
    ) = with(mediaService) {
        with(mediaLibraryDao) {
            category.syncLocalWithService(scope)
        }
    }

    override fun getMediasFlow(category: MediaCategory) =
        with(mediaLibraryDao) {
            getMediaOfCategoryFlow(category.key)
                .map { list ->
                    CategoryWithContents(
                        category,
                        list.map(MediaEntity::toDomain),
                    )
                }
        }

    override fun getContentModeFlow(): Flow<MediaContentMode> =
        userPreference.userData
            .map { it.contentMode }
            .map { modeString ->
                modeString?.deserialize() ?: MediaContentMode.ANIME
            }.distinctUntilChanged()

    override suspend fun setContentMode(mode: MediaContentMode) {
        Napier.d(tag = TAG) { "Setting content mode to: $mode" }
        userPreference.setContentMode(mode.key)
    }

    override fun syncMediaListByUserId(
        scope: CoroutineScope,
        userId: String,
        status: List<MediaListStatus>,
        mediaType: MediaType,
        scoreFormat: ScoreFormat,
    ) = context(mediaService, mediaLibraryDao) {
        // sync data from service
        syncMediaListInfoToLocal(
            userId = userId,
            status = status,
            mediaType = mediaType,
            scope = scope,
            scoreFormat = scoreFormat,
        )
    }

    override fun syncDetailMedia(
        scope: CoroutineScope,
        mediaId: String,
        voiceActorLanguage: StaffLanguage,
    ) = context(mediaService, mediaLibraryDao) {
        // sync data from service
        syncDetailMediaToLocal(
            mediaId = mediaId,
            scope = scope,
            voiceActorLanguage = voiceActorLanguage,
        )
    }

    override fun syncDetailStaff(
        scope: CoroutineScope,
        staffId: String,
    ) = context(mediaService, mediaLibraryDao) {
        // sync data from service
        syncDetailStaffToLocal(
            staffId = staffId,
            scope = scope,
        )
    }

    override fun syncMediaListItemOfUser(
        scope: CoroutineScope,
        userId: String,
        mediaId: String,
        scoreFormat: ScoreFormat,
    ) = context(mediaService, mediaLibraryDao) {
        syncMediaListOfUserToLocal(
            userId = userId,
            mediaId = mediaId,
            scope = scope,
            scoreFormat = scoreFormat,
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun getMediaListFlowByUserId(
        userId: String,
        mediaType: MediaType,
        mediaListStatus: List<MediaListStatus>,
    ): Flow<List<MediaWithMediaListItem>> =
        mediaLibraryDao
            .getMediaListFlow(
                userId = userId,
                mediaType = mediaType.key,
                listStatus = mediaListStatus.map { it.key },
            ).map {
                it
                    .map(MediaListAndMediaRelationWithUpdateLog::toDomain)
                    .sortedByDescending { it.firstAddedTime }
            }

    override fun getMediaListItemOfUserFlow(
        userId: String,
        mediaId: String,
    ): Flow<MediaListModel?> =
        mediaLibraryDao.getMediaListItemFlow(userId, mediaId).map {
            it?.toDomain()
        }

    override fun getNewReleasedAnimeListFlow(
        userId: String,
        timeSecondLaterThan: Long,
    ): Flow<List<MediaWithMediaListItem>> =
        mediaLibraryDao
            .getNewReleasedMediaListFlow(
                userId = userId,
                mediaType = MediaType.ANIME.key,
                listStatus =
                    listOf(
                        MediaListStatus.CURRENT,
                        MediaListStatus.PLANNING,
                    ).map { it.key },
                timeSecondLaterThan = timeSecondLaterThan,
            ).map {
                it.map(MediaListAndMediaRelationWithUpdateLog::toDomain)
            }

    override suspend fun loadMediaPageByCategory(
        category: MediaCategory,
        page: Int,
        perPage: Int,
    ) = with(mediaService) {
        try {
            category
                .getMediaOfCategoryFromRemote(
                    page = page,
                    perPage = perPage,
                    displayAdultContent = false,
                ).toDomain(Media::toDomain) to null
        } catch (exception: ServerException) {
            Napier.e { "Error when loading media page: $exception" }
            Page.empty<MediaModel>() to exception.toError()
        }
    }

    override suspend fun loadNotificationByPage(
        page: Int,
        perPage: Int,
        category: NotificationCategory,
        resetNotificationCount: Boolean,
    ) = with(mediaService) {
        try {
            category
                .getNotificationPage(
                    page = page,
                    perPage = perPage,
                    resetNotificationCount = resetNotificationCount,
                ).toDomain(NotificationUnion::toDomain) to null
        } catch (exception: ServerException) {
            Napier.e { "Error when loading media page: $exception" }
            Page.empty<NotificationModel>() to exception.toError()
        }
    }

    override suspend fun updateMediaListStatus(
        mediaListId: String,
        status: MediaListStatus?,
        progress: Int?,
        score: Float?,
    ): AppError? =
        MediaListModificationSyncer(mediaListId = mediaListId).postMutationAndRevertWhenException {
            var newItem = it
            if (status != null) {
                newItem = newItem.copy(mediaListStatus = status)
            }
            if (progress != null) {
                newItem = newItem.copy(progress = progress)
            }
            if (score != null) {
                newItem = newItem.copy(score = score)
            }
            newItem
        }

    override suspend fun addNewMediaToList(mediaId: String): AppError? =
        AddNewListItemSyncer(mediaId).postMutationAndRevertWhenException(syncWhenItemChanged = false)

    override suspend fun searchMediaFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Media,
    ) = try {
        mediaService
            .searchMedia(
                page = page,
                perPage = perPage,
                keyword = searchSource.keyword,
                type = searchSource.type.toServiceType(),
                season = (searchSource as? SearchSource.Media.Anime)?.season?.toServiceType(),
                seasonYear = (searchSource as? SearchSource.Media.Anime)?.seasonYear?.toInt(),
                formatIn = (searchSource as? SearchSource.Media.Anime)?.mediaFormat?.map(MediaFormat::toServiceType),
                isAdult = false,
            ).toDomain(Media::toDomain) to null
    } catch (exception: ServerException) {
        Napier.e { "Error when loading media page: $exception" }
        Page.empty<MediaModel>() to exception.toError()
    }

    override suspend fun searchCharacterFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Character,
    ) = try {
        mediaService
            .searchCharacter(
                page = page,
                perPage = perPage,
                keyword = searchSource.keyword,
            ).toDomain(Character::toDomain) to null
    } catch (exception: ServerException) {
        Napier.e { "Error when loading media page: $exception" }
        Page.empty<CharacterModel>() to exception.toError()
    }

    override suspend fun searchStaffFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Staff,
    ) = try {
        mediaService
            .searchStaff(
                page = page,
                perPage = perPage,
                keyword = searchSource.keyword,
            ).toDomain(Staff::toDomain) to null
    } catch (exception: ServerException) {
        Napier.e { "Error when loading media page: $exception" }
        Page.empty<StaffModel>() to exception.toError()
    }

    override suspend fun searchStudioFromSource(
        page: Int,
        perPage: Int,
        searchSource: SearchSource.Studio,
    ) = try {
        mediaService
            .searchStudio(
                page = page,
                perPage = perPage,
                keyword = searchSource.keyword,
            ).toDomain(Studio::toDomain) to null
    } catch (exception: ServerException) {
        Napier.e { "Error when loading media page: $exception" }
        Page.empty<StudioModel>() to exception.toError()
    }

    override fun getMediaFlow(mediaId: String): Flow<MediaModel> = mediaLibraryDao.getMediaFlow(mediaId).map(MediaEntity::toDomain)

    override fun getStudioOfMediaFlow(mediaId: String): Flow<List<StudioModel>> =
        mediaLibraryDao
            .getStudiosOfMediaFlow(mediaId)
            .map { entities ->
                entities
                    .filter { it.isAnimationStudio == true }
                    .map(StudioEntity::toDomain)
            }

    override fun getStaffOfMediaFlow(mediaId: String): Flow<List<StaffWithRole>> =
        mediaLibraryDao.getStaffOfMediaFlow(mediaId).map {
            it.map(me.andannn.aniflow.database.relation.StaffWithRole::toDomain)
        }

    override fun getRelationsOfMediaFlow(mediaId: String): Flow<List<MediaModelWithRelationType>> =
        mediaLibraryDao.getRelatedMediaOfMediaFlow(mediaId).map {
            it.map(MediaEntityWithRelationType::toDomain)
        }

    override fun getCharactersOfMediaFlow(
        mediaId: String,
        language: StaffLanguage,
    ): Flow<List<CharacterWithVoiceActor>> =
        mediaLibraryDao.getCharacterWithVoiceActorOfMediaFlow(mediaId, language.key).map {
            it.map {
                it.toDomain(language)
            }
        }

    override suspend fun toggleMediaItemLike(
        mediaId: String,
        mediaType: MediaType,
    ): AppError? =
        ToggleMediaLikeSyncer(mediaId, mediaType).postMutationAndRevertWhenException { old ->
            old.copy(
                isFavourite = !(old.isFavourite ?: false),
            )
        }

    override suspend fun toggleStaffItemLike(staffId: String): AppError? =
        ToggleStaffLikeSyncer(staffId).postMutationAndRevertWhenException { old ->
            old.copy(
                isFavourite = !(old.isFavourite ?: false),
            )
        }

    override suspend fun toggleCharacterItemLike(characterId: String): AppError? =
        ToggleCharacterLikeSyncer(characterId).postMutationAndRevertWhenException { old ->
            old.copy(
                isFavourite = !(old.isFavourite ?: false),
            )
        }

    override suspend fun getStaffPageOfMedia(
        mediaId: String,
        page: Int,
        perPage: Int,
    ): Pair<Page<StaffWithRole>, AppError?> =
        try {
            mediaService
                .getDetailMedia(
                    id = mediaId.toInt(),
                    staffPage = page,
                    staffPerPage = perPage,
                ).media.staff!!
                .toPage()
                .toDomain(StaffConnection.Edge::toDomain) to null
        } catch (exception: ServerException) {
            Napier.e { "Error when loading staff page: $exception" }
            Page.empty<StaffWithRole>() to exception.toError()
        }

    override suspend fun getCharacterPageOfMedia(
        mediaId: String,
        characterStaffLanguage: StaffLanguage,
        page: Int,
        perPage: Int,
    ): Pair<Page<CharacterWithVoiceActor>, AppError?> =
        try {
            mediaService
                .getDetailMedia(
                    id = mediaId.toInt(),
                    characterPage = page,
                    characterPerPage = perPage,
                    characterStaffLanguage = characterStaffLanguage.toServiceType(),
                ).media.characters!!
                .toPage()
                .toDomain {
                    it.toDomain(characterStaffLanguage)
                } to null
        } catch (exception: ServerException) {
            Napier.e { "Error when loading Character page: $exception" }
            Page.empty<CharacterWithVoiceActor>() to exception.toError()
        }

    override fun getDetailStaff(staffId: String): Flow<StaffModel> = mediaLibraryDao.getStaffFlow(staffId).map(StaffEntity::toDomain)

    override fun getDetailCharacter(characterId: String): Flow<CharacterModel> =
        mediaLibraryDao.getCharacterFlow(characterId).map(CharacterEntity::toDomain)

    override fun syncDetailCharacter(
        scope: CoroutineScope,
        characterId: String,
    ): Deferred<Throwable?> =
        context(mediaService, mediaLibraryDao) {
            // sync data from service
            syncDetailCharacterToLocal(
                characterId = characterId,
                scope = scope,
            )
        }

    override suspend fun getMediaPageOfStaff(
        staffId: String,
        page: Int,
        perPage: Int,
        mediaSort: MediaSort,
    ): Pair<Page<VoicedCharacterWithMedia>, AppError?> =
        try {
            val page =
                mediaService
                    .getStaffDetail(
                        staffId = staffId.toInt(),
                        characterConnectionPage = page,
                        characterConnectionPerPage = perPage,
                        mediaSort = listOf(mediaSort.toServiceType()),
                    )?.characterMedia
                    ?.toPage()
                    ?.toDomainWithFlattenMapper(MediaConnection.Edge::toDomain)
                    ?: Page.empty()
            page to null
        } catch (exception: ServerException) {
            Napier.e { "Error when loading Character page: $exception" }
            Page.empty<VoicedCharacterWithMedia>() to exception.toError()
        }

    override suspend fun getMediaPageOfCharacter(
        character: String,
        page: Int,
        perPage: Int,
        mediaSort: MediaSort,
    ): Pair<Page<MediaModel>, AppError?> =
        try {
            val page =
                mediaService
                    .getCharacterDetail(
                        id = character.toInt(),
                        mediaConnectionPage = page,
                        mediaConnectionPerPage = perPage,
                        mediaSort = listOf(mediaSort.toServiceType()),
                    )?.media
                    ?.toMediaPage()
                    ?.toDomain(Media::toDomain)
                    ?: Page.empty()
            page to null
        } catch (exception: ServerException) {
            Napier.e { "Error when loading Character page: $exception" }
            Page.empty<MediaModel>() to exception.toError()
        }
}

private const val DEFAULT_CACHED_SIZE = 20

context(service: AniListService, database: MediaLibraryDao)
private fun syncMediaListInfoToLocal(
    userId: String,
    status: List<MediaListStatus>,
    mediaType: MediaType,
    scoreFormat: ScoreFormat,
    scope: CoroutineScope,
): Deferred<Throwable?> =
    scope.async {
        Napier.d(tag = TAG) { "syncMediaListInfoToLocal start: userId=$userId, status=$status, mediaType=$mediaType" }
        try {
            val mediaList =
                fetchAllMediaList(
                    userId = userId,
                    status = status,
                    mediaType = mediaType,
                    scoreFormat = scoreFormat,
                )
            database.upsertMediaListEntities(mediaList.map(MediaList::toRelation))
            Napier.d(tag = TAG) { "syncMediaListInfoToLocal finished" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService, database: MediaLibraryDao)
private fun syncDetailCharacterToLocal(
    characterId: String,
    scope: CoroutineScope,
): Deferred<Throwable?> =
    scope.async {
        Napier.d(tag = TAG) { "syncDetailCharacterToLocal start: characterId=$characterId" }
        try {
            val character = service.getCharacterDetail(id = characterId.toInt())?.toEntity()
            if (character != null) {
                database.upsertCharacter(character)
            }

            Napier.d(tag = TAG) { "syncDetailCharacterToLocal finished" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService, database: MediaLibraryDao)
private fun syncDetailStaffToLocal(
    staffId: String,
    scope: CoroutineScope,
): Deferred<Throwable?> =
    scope.async {
        Napier.d(tag = TAG) { "syncDetailStaffToLocal start: staffId=$staffId" }
        try {
            val staff = service.getStaffDetail(staffId = staffId.toInt())?.toEntity()
            if (staff != null) {
                database.upsertStaff(staff)
            }

            Napier.d(tag = TAG) { "syncDetailStaffToLocal finished" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService, database: MediaLibraryDao)
private fun syncDetailMediaToLocal(
    mediaId: String,
    voiceActorLanguage: StaffLanguage,
    scope: CoroutineScope,
): Deferred<Throwable?> =
    scope.async {
        Napier.d(tag = TAG) { "syncDetailMediaToLocal start: staffId=$mediaId" }
        try {
            val detailMedia =
                service
                    .getDetailMedia(
                        id = mediaId.toInt(),
                        withStudioConnection = true,
                        withRelationConnection = true,
                        staffPage = 1,
                        staffPerPage = 9,
                        characterPage = 1,
                        characterPerPage = 9,
                        characterStaffLanguage = voiceActorLanguage.toServiceType(),
                    ).media
            Napier.d(tag = TAG) { "syncDetailMediaToLocal finished" }

            database.upsertMedias(listOf(detailMedia.toEntity()))
            database.upsertStudiosOfMedia(
                detailMedia.id.toString(),
                detailMedia.studios
                    ?.nodes
                    ?.filterNotNull()
                    ?.map(Studio::toEntity) ?: emptyList(),
            )
            database.upsertStaffOfMedia(
                detailMedia.id.toString(),
                detailMedia.staff
                    ?.edges
                    ?.filterNotNull()
                    ?.map(StaffConnection.Edge::toEntity) ?: emptyList(),
            )

            database.upsertMediaRelations(
                detailMedia.id.toString(),
                detailMedia.relations
                    ?.edges
                    ?.filterNotNull()
                    ?.map(MediaRelations.Edge::toEntity)
                    ?: emptyList(),
            )

            database.upsertCharacterWithVoiceActorRelation(
                detailMedia.id.toString(),
                voiceActorLanguage.key,
                detailMedia.characters
                    ?.edges
                    ?.filterNotNull()
                    ?.map(CharactersConnection.Edge::toEntity)
                    ?: emptyList(),
            )

            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService, database: MediaLibraryDao)
private fun syncMediaListOfUserToLocal(
    mediaId: String,
    userId: String,
    scope: CoroutineScope,
    scoreFormat: ScoreFormat,
): Deferred<Throwable?> =
    scope.async {
        Napier.d(tag = TAG) { "syncMediaListOfUserToLocal start: staffId=$mediaId, userId=$userId" }
        try {
            Napier.d(tag = TAG) { "syncMediaListOfUserToLocal finished" }

            val mediaListItem =
                service.getMediaListItem(
                    mediaId = mediaId.toInt(),
                    userId = userId.toInt(),
                    scoreFormat = scoreFormat.toServiceType(),
                ) ?: return@async null
            database.upsertMediaListEntity(mediaListItem.toEntity(mediaId))
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService)
private suspend fun fetchAllMediaList(
    userId: String,
    status: List<MediaListStatus>,
    mediaType: MediaType,
    scoreFormat: ScoreFormat,
): List<MediaList> {
    val acc = mutableListOf<MediaList>()
    var page = 1

    var pageCount = 0

    do {
        val res =
            service.getMediaListPage(
                userId = userId.toInt(),
                page = page++,
                perPage = 50,
                statusIn = status.map(MediaListStatus::toServiceType),
                type = mediaType.toServiceType(),
                format = scoreFormat.toServiceType(),
            )
        acc += res.items
        pageCount++
    } while (res.pageInfo?.hasNextPage == true && pageCount <= 5)

    return acc
}

context(service: AniListService, database: MediaLibraryDao)
private fun MediaCategory.syncLocalWithService(scope: CoroutineScope): Deferred<Throwable?> =
    scope.async {
        val category = this@syncLocalWithService
        Napier.d(tag = TAG) { "syncLocalWithService start: $category" }
        try {
            val items =
                getMediaOfCategoryFromRemote(
                    page = 1,
                    perPage = DEFAULT_CACHED_SIZE,
                    displayAdultContent = false,
                ).items
            database.upsertMediasWithCategory(
                category = category.key,
                mediaList = items.map(Media::toEntity),
            )
            Napier.d(tag = TAG) { "syncLocalWithService finished: $category. size ${items.size}" }
            null
        } catch (exception: ServerException) {
            Napier.e { "Error when syncing local with remote: $exception" }
            exception
        }
    }

context(service: AniListService)
private suspend fun MediaCategory.getMediaOfCategoryFromRemote(
    page: Int,
    perPage: Int,
    displayAdultContent: Boolean,
) = run {
    var status: MediaStatus?
    var seasonParam: AnimeSeasonParam?
    val type = this.mediaType()
    var sorts: List<MediaSort>? = null
    var format: List<MediaFormat>? = null
    var code: String? = null

    val currentSeasonParam = currentSeasonByLocalDataTime()

    when (this) {
        MediaCategory.CURRENT_SEASON_ANIME -> {
            status = null
            seasonParam = currentSeasonParam
            format =
                listOf(
                    MediaFormat.TV,
                    MediaFormat.TV_SHORT,
                    MediaFormat.OVA,
                    MediaFormat.ONA,
                    MediaFormat.ONE_SHOT,
                )
        }

        MediaCategory.NEXT_SEASON_ANIME -> {
            status = null
            seasonParam = currentSeasonParam.nextSeasonParam()
            format =
                listOf(
                    MediaFormat.TV,
                    MediaFormat.TV_SHORT,
                    MediaFormat.OVA,
                    MediaFormat.ONA,
                    MediaFormat.ONE_SHOT,
                )
        }

        MediaCategory.TRENDING_ANIME -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.MOVIE_ANIME -> {
            status = null
            seasonParam = null
            format = listOf(MediaFormat.MOVIE)
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.TRENDING_MANGA -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.TRENDING_DESC)
        }

        MediaCategory.ALL_TIME_POPULAR_MANGA -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.POPULARITY_DESC)
        }

        MediaCategory.TOP_MANHWA -> {
            status = null
            seasonParam = null
            code = "KR"
            sorts = listOf(MediaSort.POPULARITY_DESC)
        }

        MediaCategory.NEW_ADDED_ANIME -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.START_DATE_DESC)
        }

        MediaCategory.NEW_ADDED_MANGA -> {
            status = null
            seasonParam = null
            sorts = listOf(MediaSort.START_DATE_DESC)
        }
    }

    service
        .getMediaPage(
            page = page,
            perPage = perPage,
            seasonYear = seasonParam?.seasonYear,
            season = seasonParam?.season?.toServiceType(),
            status = status,
            countryCode = code,
            isAdult = displayAdultContent,
            type = type.toServiceType(),
            formatIn = format?.map { it.toServiceType() },
            sort = sorts?.map { it.toServiceType() },
        )
}

context(service: AniListService)
private suspend fun NotificationCategory.getNotificationPage(
    page: Int,
    perPage: Int,
    resetNotificationCount: Boolean,
) = run {
    val category = this
    val types =
        when (category) {
            NotificationCategory.ALL -> NotificationType.entries
            NotificationCategory.AIRING ->
                listOf(
                    NotificationType.AIRING,
                )

            NotificationCategory.ACTIVITY ->
                listOf(
                    NotificationType.ACTIVITY_LIKE,
                    NotificationType.ACTIVITY_REPLY,
                    NotificationType.ACTIVITY_MENTION,
                    NotificationType.ACTIVITY_REPLY_LIKE,
                    NotificationType.ACTIVITY_MESSAGE,
                )

            NotificationCategory.FOLLOWS ->
                listOf(
                    NotificationType.FOLLOWING,
                )

            NotificationCategory.MEDIA ->
                listOf(
                    NotificationType.MEDIA_DATA_CHANGE,
                    NotificationType.RELATED_MEDIA_ADDITION,
                    NotificationType.MEDIA_MERGE,
                )
        }

    service.getNotificationPage(
        page = page,
        perPage = perPage,
        notificationTypeIn = types,
        resetNotificationCount = resetNotificationCount,
    )
}
