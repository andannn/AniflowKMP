/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.getNameString

enum class BottomBarState {
    NEED_LOGIN,
    AUTHED_WITHOUT_LIST_ITEM,
    AUTHED_WITH_LIST_ITEM,
}

data class DetailStaffUiState(
    val userOption: UserOptions,
    val staffModel: StaffModel?,
) {
    val title
        get() = staffModel?.name.getNameString(userOption.staffNameLanguage)

    companion object {
        val Empty = DetailStaffUiState(UserOptions.Default, null)
    }
}

interface DetailStaffUiDataProvider : DataProvider {
    val staffId: String

    @NativeCoroutines
    fun uiDataFlow(): Flow<DetailStaffUiState>
}
