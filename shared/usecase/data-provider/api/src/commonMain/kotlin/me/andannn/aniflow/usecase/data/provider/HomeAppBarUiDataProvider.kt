/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaContentMode

data class HomeAppBarUiState(
    val authedUser: UserModel? = null,
    val contentMode: MediaContentMode = MediaContentMode.ANIME,
) {
    companion object {
        val Empty = HomeAppBarUiState()
    }
}

interface HomeAppBarUiDataProvider {
    @NativeCoroutines
    fun appBarFlow(): Flow<HomeAppBarUiState>
}
