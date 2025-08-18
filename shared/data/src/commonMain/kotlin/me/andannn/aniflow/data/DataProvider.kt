/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.service.ServerException

sealed class AppError(
    open val message: String,
) {
    data class RemoteSyncError(
        override val message: String,
    ) : AppError(message)

    data class OtherError(
        override val message: String,
    ) : AppError(message)
}

data class DiscoverUiState(
    val categoryDataMap: CategoryDataModel = CategoryDataModel(),
    val authedUser: UserModel? = null,
) {
    companion object {
        val Empty = DiscoverUiState()
    }
}

data class TrackUiState(
    val items: List<MediaWithMediaListItem> = emptyList(),
) {
    companion object {
        val Empty = TrackUiState()
    }
}

class DataProvider(
    private val mediaRepo: MediaRepository,
    private val authRepo: AuthRepository,
) {
    @NativeCoroutines
    fun discoverUiDataFlow(): Flow<DiscoverUiState> =
        with(mediaRepo) {
            with(authRepo) {
                return discoverUiStateFlow()
            }
        }

    @NativeCoroutines
    fun discoverUiSideEffect(): Flow<AppError> =
        flow {
            val categories = MediaType.ANIME.allCategories()
            supervisorScope {
                val deferredList =
                    categories.map { category ->
                        mediaRepo.syncMediaCategory(this, category)
                    }
                val errors =
                    deferredList
                        .awaitAll()
                        .filterNotNull()
                        .map(Throwable::toError)
                        .distinct()

                for (error in errors) {
                    emit(error)
                }
            }
        }

    @NativeCoroutines
    fun trackUiDataFlow(): Flow<TrackUiState> = with(mediaRepo) {
        with(authRepo) {
            return trackUiStateFlow()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutines
    fun trackUiSideEffect(): Flow<AppError> =
        flow {
            authRepo.getAuthedUser().distinctUntilChanged().collectLatest { authedUser ->
                if (authedUser != null) {
                    coroutineScope {
                        val deferred = mediaRepo.syncMediaListByUserId(
                            scope = this,
                            userId = authedUser.id,
                            status = listOf(
                                MediaListStatus.PLANNING,
                                MediaListStatus.CURRENT,
                            ),
                            mediaType = MediaType.ANIME,
                        )

                        deferred.await()?.let {
                            emit(it.toError())
                        }
                    }
                }
            }
        }

}

context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun discoverUiStateFlow(): Flow<DiscoverUiState> {
    val allCategories = MediaType.ANIME.allCategories()
    val dataFlowList =
        allCategories.map { category ->
            mediaRepo.getMediasFlow(category)
        }
    val categoryDataFlow =
        combine(
            dataFlowList,
        ) {
            CategoryDataModel(it.toList())
        }
    val authedUserFlow = authRepo.getAuthedUser()

    return combine(
        categoryDataFlow,
        authedUserFlow,
    ) { categoryData, authedUser ->
        DiscoverUiState(
            categoryDataMap = categoryData,
            authedUser = authedUser,
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
context(mediaRepo: MediaRepository, authRepo: AuthRepository)
private fun trackUiStateFlow(): Flow<TrackUiState> {
    val trackUiFlow = authRepo
        .getAuthedUser()
        .flatMapLatest { authUser ->
            if (authUser == null) {
                // If not authenticated, return an empty flow
                emptyFlow()
            } else {
                mediaRepo.getMediaListFlowByUserId(
                    userId = authUser.id,
                    mediaListStatus =
                        listOf(
                            MediaListStatus.PLANNING,
                            MediaListStatus.CURRENT,
                        ),
                    mediaType = MediaType.ANIME,
                )
            }
        }
    return trackUiFlow.map {
        TrackUiState(
            items = it,
        )
    }
}

private fun Throwable.toError(): AppError =
    when (this) {
        is ServerException -> AppError.RemoteSyncError(message)
        else -> AppError.OtherError(message ?: "Unknown error")
    }
