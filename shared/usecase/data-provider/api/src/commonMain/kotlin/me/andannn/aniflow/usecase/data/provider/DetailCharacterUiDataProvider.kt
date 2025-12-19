/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.getNameString

data class DetailCharacterUiState(
    val userOption: UserOptions,
    val characterModel: CharacterModel?,
) {
    val title
        get() = characterModel?.name.getNameString(userOption.staffNameLanguage)

    companion object {
        val Empty = DetailCharacterUiState(UserOptions.Default, null)
    }
}

interface DetailCharacterUiDataProvider : DataProvider {
    val characterId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailCharacterUiState>
}
