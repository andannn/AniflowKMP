/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.components.discover

import com.arkivanov.decompose.value.Value
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.UserModel

interface DiscoverComponent {
    val categoryDataMap: Value<CategoryDataModel>

    val authedUser: Value<Optional<UserModel>>

    fun onStartLoginProcess()
}

data class CategoryDataModel(
    val map: Map<MediaCategory, List<MediaModel>> = emptyMap(),
)

data class Optional<T>(
    val value: T?,
)
