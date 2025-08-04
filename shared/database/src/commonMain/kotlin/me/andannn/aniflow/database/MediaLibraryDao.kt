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
import me.andannn.aniflow.database.schema.MediaEntity
import me.andannn.aniflow.database.schema.UserEntity

class MediaLibraryDao(
    private val aniflowDatabase: AniflowDatabase,
) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun getMediaById(mediaId: String): MediaEntity? =
        withDatabase {
            mediaQueries.getMedia(mediaId).awaitAsOneOrNull()
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
            mediaQueries.getMediaListById(mediaIds).awaitAsList()
        }

    suspend fun upsertMedias(mediaList: List<MediaEntity>) =
        withDatabase {
            transaction(true) {
                mediaList.forEach { mediaEntity ->
                    mediaQueries.upsertMedia(mediaEntity)
                }
            }
        }

    suspend fun upsertMediasWithCategory(
        category: String,
        mediaList: List<MediaEntity>,
    ) = withDatabase {
        transaction(true) {
            mediaList.forEach { mediaEntity ->
                mediaQueries.upsertMedia(mediaEntity)
                mediaCategoryWithMediaIdEntityQueries.upsertMediaCategoryWithMediaId(
                    category = category,
                    mediaId = mediaEntity.id,
                )
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
            transaction(true) {
                userList.forEach { user ->
                    userQueries.upsertUser(user)
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

    private inline fun <T> withDatabase(block: AniflowDatabase.() -> T): T = block.invoke(aniflowDatabase)
}
