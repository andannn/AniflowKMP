/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.relation.CategoryDataModel
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.service.ServerException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class SyncStatus {
    data object Loading : SyncStatus()
    data class Idle(val errors: List<AppError> = emptyList()) : SyncStatus()

    fun isLoading(): Boolean = this is Loading
}

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
    fun discoverUiSideEffect(): Flow<SyncStatus> = createSideEffectFlow(
        RefreshAllCategoriesTask(),
        SyncUserMediaListTask()
    )

    @NativeCoroutines
    fun trackUiDataFlow(): Flow<TrackUiState> = with(mediaRepo) {
        with(authRepo) {
            return trackUiStateFlow()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @NativeCoroutines
    fun trackUiSideEffect(): Flow<SyncStatus> = createSideEffectFlow(
        SyncUserMediaListTask()
    )
}

private const val TAG = "DataProvider"

private fun createSideEffectFlow(
    vararg tasks: SideEffectTask<SyncStatus>,
) = channelFlow {
    supervisorScope {
        tasks.forEach { task ->
            launch {
                with(task) {
                    run()
                }
            }
        }
    }
}.aggregateStatus()

private fun Flow<SyncStatus>.aggregateStatus(): Flow<SyncStatus> = flow {
    var active = 0
    val pendingErrors = mutableListOf<AppError>()

    collect { status ->
        when (status) {
            is SyncStatus.Loading -> {
                if (active == 0) emit(SyncStatus.Loading)
                active += 1
            }

            is SyncStatus.Idle -> {
                if (status.errors.isNotEmpty()) pendingErrors += status.errors
                active = (active - 1).coerceAtLeast(0)
                if (active == 0) {
                    emit(SyncStatus.Idle(pendingErrors.toList()))
                    pendingErrors.clear()
                }
            }
        }
    }
}

private interface SideEffectTask<T> {
    suspend fun ProducerScope<T>.run()
}

private class SyncUserMediaListTask : SideEffectTask<SyncStatus>, KoinComponent {
    private val mediaRepo: MediaRepository by inject()
    private val authRepo: AuthRepository by inject()

    override suspend fun ProducerScope<SyncStatus>.run() {
        Napier.d(tag = TAG) { "SyncUserMediaListTask run" }
        authRepo
            .getAuthedUser()
            .distinctUntilChanged()
            .collectLatest { authedUser ->
                if (authedUser != null) {
                    send(SyncStatus.Loading)
                    val deferred = mediaRepo.syncMediaListByUserId(
                        scope = this,
                        userId = authedUser.id,
                        status = listOf(
                            MediaListStatus.PLANNING,
                            MediaListStatus.CURRENT,
                        ),
                        mediaType = MediaType.ANIME,
                    )

                    val error = deferred.await()

                    if (error == null) {
                        send(SyncStatus.Idle())
                    } else {
                        send(SyncStatus.Idle(listOf(error.toError())))
                    }
                }
            }
    }
}

private class RefreshAllCategoriesTask : SideEffectTask<SyncStatus>, KoinComponent {
    private val mediaRepo: MediaRepository by inject()

    override suspend fun ProducerScope<SyncStatus>.run() {
        Napier.d(tag = TAG) { "RefreshAllCategoriesTask run" }
        send(SyncStatus.Loading)
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

            send(SyncStatus.Idle(errors))
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
