/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem

data class DiscoverUiState(
    val categoryDataMap: CategoryDataModel = CategoryDataModel(),
    val newReleasedMedia: List<MediaWithMediaListItem> = emptyList(),
    val userOptions: UserOptions = UserOptions.Default,
) {
    companion object {
        val Empty = DiscoverUiState()
    }
}

/**
 * Provides data for the Discover UI components.
 */
interface DiscoverUiDataProvider : DataProvider {
    @NativeCoroutines
    fun uiDataFlow(): Flow<DiscoverUiState>
}
