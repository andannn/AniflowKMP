/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

interface MediaRepository {
    fun syncMediaCategory(
        scope: CoroutineScope,
        category: MediaCategory,
    ): Deferred<Throwable?>

    fun getMediasFlow(category: MediaCategory): Flow<CategoryWithContents>

    fun syncMediaListByUserId(
        scope: CoroutineScope,
        userId: String,
        status: List<MediaListStatus>,
        mediaType: MediaType,
    ): Deferred<Throwable?>

    fun getMediaListFlowByUserId(
        userId: String,
        mediaType: MediaType,
        mediaListStatus: List<MediaListStatus>,
    ): Flow<List<MediaWithMediaListItem>>
}
