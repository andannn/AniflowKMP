/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.database

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import me.andannn.aniflow.database.relation.MediaListAndMediaRelation
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.UserEntity

class MediaLibraryDao(
    private val aniflowDatabase: AniflowDatabase,
) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getMediaById(mediaId: String): MediaEntity? =
        withDatabase {
            withContext(dispatcher) { mediaQueries.getMedia(mediaId).awaitAsOneOrNull() }
        }

    fun getMediaFlow(mediaId: String): Flow<MediaEntity> =
        withDatabase {
            mediaQueries
                .getMedia(mediaId)
                .asFlow()
                .mapToOneOrNull(dispatcher)
                .filterNotNull()
        }

    suspend fun getMediasById(mediaIds: List<String>): List<MediaEntity> =
        withDatabase {
            withContext(dispatcher) {
                mediaQueries.getMediaListById(mediaIds).awaitAsList()
            }
        }

    suspend fun upsertMedias(mediaList: List<MediaEntity>) =
        withDatabase {
            withContext(dispatcher) {
                transaction(true) {
                    mediaList.forEach { mediaEntity ->
                        mediaQueries.upsertMedia(mediaEntity)
                    }
                }
            }
        }

    suspend fun upsertMediasWithCategory(
        category: String,
        mediaList: List<MediaEntity>,
    ) = withDatabase {
        withContext(dispatcher) {
            transaction(true) {
                mediaCategoryWithMediaIdEntityQueries.deleteByCategory(category)
                mediaList.forEach { mediaEntity ->
                    mediaQueries.upsertMedia(mediaEntity)
                    mediaCategoryWithMediaIdEntityQueries.upsertMediaCategoryWithMediaId(
                        category = category,
                        mediaId = mediaEntity.id,
                    )
                }
            }
        }
    }

    fun getMediaOfCategoryFlow(category: String): Flow<List<MediaEntity>> =
        withDatabase {
            mediaCategoryWithMediaIdEntityQueries
                .getMediaOfCategory(category)
                .asFlow()
                .mapToList(dispatcher)
        }

    suspend fun upsertUser(userList: List<UserEntity>) =
        withDatabase {
            withContext(dispatcher) {
                transaction(true) {
                    userList.forEach { user ->
                        userQueries.upsertUser(user)
                    }
                }
            }
        }

    fun getUserFlow(userId: String): Flow<UserEntity> =
        withDatabase {
            userQueries
                .getUserById(userId)
                .asFlow()
                .mapToOneOrNull(dispatcher)
                .filterNotNull()
        }

    suspend fun upsertMediaListEntities(mediaListEntities: List<MediaListAndMediaRelation>) =
        withDatabase {
            withContext(dispatcher) {
                transaction(true) {
                    mediaListEntities.forEach { mediaListAndMediaRelation ->
                        mediaListQueries.upsertMediaList(mediaListAndMediaRelation.mediaListEntity)
                        mediaQueries.upsertMedia(mediaListAndMediaRelation.mediaEntity)
                    }
                }
            }
        }

    fun getMediaListFlow(
        userId: String,
        mediaType: String,
        listStatus: List<String>,
    ): Flow<List<MediaListAndMediaRelation>> =
        withDatabase {
            mediaListQueries
                .getMediaListAndMediaRelationList(
                    userId = userId,
                    mediaType = mediaType,
                    listStatus = listStatus,
                    mapper = MediaListAndMediaRelation::mapTo,
                ).asFlow()
                .mapToList(dispatcher)
        }

    private inline fun <T> withDatabase(block: AniflowDatabase.() -> T): T = block.invoke(aniflowDatabase)
}
