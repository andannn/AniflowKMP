/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import me.andannn.aniflow.data.model.DataWithError
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.CategoryDataModel

data class DiscoverUiState(
    val categoryDataMap: CategoryDataModel = CategoryDataModel(),
    val authedUser: UserModel? = null,
) {
    companion object {
        val Empty = DiscoverUiState()
    }
}

class DataProvider(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) {
    @NativeCoroutines
    fun discoverUiDataFlow(): Flow<DataWithError<DiscoverUiState>> =
        with(mediaRepo) {
            with(authRepo) {
                return discoverUiStateFlow()
            }
        }
}

context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun discoverUiStateFlow(): Flow<DataWithError<DiscoverUiState>> {
    val allCategories = MediaType.ANIME.allCategories()
    val dataWithErrorFlowList =
        allCategories.map { category ->
            mediaRepo.getMediasFlow(category)
        }
    val errorFlow =
        dataWithErrorFlowList
            .map { it.errorFlow }
            .merge()
    val categoryDataFlow =
        combine(
            dataWithErrorFlowList.map { it.dataFlow },
        ) {
            CategoryDataModel(it.toList())
        }
    val authedUserFlow = authRepo.getAuthedUser()

    return combine(
        categoryDataFlow,
        authedUserFlow,
        errorFlow,
    ) { categoryData, authedUser, error ->
        DataWithError(
            data =
                DiscoverUiState(
                    categoryDataMap = categoryData,
                    authedUser = authedUser,
                ),
            error = error,
        )
    }
}
