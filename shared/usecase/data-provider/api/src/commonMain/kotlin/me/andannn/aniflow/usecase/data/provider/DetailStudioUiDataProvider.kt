/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.UserOptions

data class DetailStudioState(
    val userOption: UserOptions,
    val studioModel: StudioModel?,
) {
    companion object {
        val Empty = DetailStudioState(UserOptions.Default, null)
    }
}

interface DetailStudioUiDataProvider : DataProvider {
    val studioId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailStudioState>
}
