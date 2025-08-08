/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.DataWithErrors
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.Page
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

interface MediaRepository {
    fun getAllMediasWithCategoryFlow(mediaType: MediaType): Flow<DataWithErrors<Map<MediaCategory, List<MediaModel>>>>

    fun getMediaListFlowByUserId(
        userId: String,
        mediaType: MediaType,
        mediaListStatus: List<MediaListStatus>,
    ): Flow<DataWithErrors<List<MediaWithMediaListItem>>>

    suspend fun loadMediaPageByCategory(
        category: MediaCategory,
        page: Int,
        perPage: Int,
    ): Page<MediaModel>
}
