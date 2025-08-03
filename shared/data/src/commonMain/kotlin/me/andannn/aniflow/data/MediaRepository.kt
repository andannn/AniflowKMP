/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.DataWithErrors
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaType

interface MediaRepository {
    fun getAllMediasWithCategory(mediaType: MediaType): Flow<DataWithErrors<Map<MediaCategory, List<MediaModel>>>>
}
