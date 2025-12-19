/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.provider.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.database.MediaLibraryDao
import me.andannn.aniflow.usecase.data.provider.SyncStatus
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal const val TAG = "SideEffectTask"

/**
 * Create a [Flow] that runs the provided [tasks] and aggregates their [SyncStatus].
 *
 * @param forceRefreshFirstTime If true, all tasks will refresh data regardless of the last refresh time on the first run.
 * @param tasks The tasks to run.
 */
internal fun createSideEffectFlow(
    forceRefreshFirstTime: Boolean,
    vararg tasks: SideEffectTask<SyncStatus>,
) = channelFlow {
    supervisorScope {
        tasks.forEach { task ->
            launch {
                with(task) {
                    WrappedProducerScope(this@channelFlow).register(forceRefreshFirstTime)
                }
            }
        }
    }
}.aggregateStatus()

@Serializable
internal sealed class TaskRefreshKey {
    @Serializable
    data class AllCategories(
        val mediaContentMode: MediaContentMode,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncUserMediaList(
        val mediaContentMode: MediaContentMode,
        val userId: String,
        val scoreFormat: ScoreFormat,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncUserCondition(
        val userId: String,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncMediaListItem(
        val userId: String,
        val mediaId: String,
        val scoreFormat: ScoreFormat,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncDetailMediaItem(
        val mediaId: String,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncDetailStaff(
        val staffId: String,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncDetailStudio(
        val studioId: String,
    ) : TaskRefreshKey()

    @Serializable
    data class SyncDetailCharacter(
        val characterId: String,
    ) : TaskRefreshKey()

    fun key() = Json.encodeToString(this)
}

internal interface SideEffectTask<T> {
    /**
     * Run the task in a [WrappedProducerScope].
     *
     * @param forceRefresh If true, the task will refresh data regardless of the last refresh time.
     */
    suspend fun WrappedProducerScope<T>.register(forceRefresh: Boolean)
}

internal class WrappedProducerScope<T>(
    internal val producerScope: ProducerScope<DataWithKey<T>>,
) {
    suspend fun send(
        refreshKey: TaskRefreshKey,
        value: T,
    ) {
        producerScope.send(DataWithKey(refreshKey, value))
    }
}

internal data class DataWithKey<T>(
    val key: TaskRefreshKey,
    val data: T,
)

internal fun Flow<DataWithKey<SyncStatus>>.aggregateStatus(): Flow<SyncStatus> =
    flow {
        val activeKeys = mutableSetOf<Any>()
        val pendingErrors = mutableListOf<AppError>()
        var hasEmittedLoading = false

        collect { (key, status) ->
            when (status) {
                is SyncStatus.Loading -> {
                    val wasEmpty = activeKeys.isEmpty()
                    if (activeKeys.add(key)) {
                        if (wasEmpty && !hasEmittedLoading) {
                            emit(SyncStatus.Loading)
                            hasEmittedLoading = true
                        }
                    }
                }

                is SyncStatus.Idle -> {
                    if (status.errors.isNotEmpty()) {
                        pendingErrors += status.errors
                    }

                    if (activeKeys.remove(key) && activeKeys.isEmpty()) {
                        emit(SyncStatus.Idle(pendingErrors.toList()))
                        pendingErrors.clear()
                        hasEmittedLoading = false
                    }
                }
            }
        }
    }

context(_: MediaLibraryDao)
internal suspend inline fun WrappedProducerScope<SyncStatus>.doRefreshIfNeeded2(
    taskRefreshKey: TaskRefreshKey,
    force: Boolean,
    crossinline block: suspend () -> AppError?,
) {
    doRefreshIfNeeded(taskRefreshKey, force) {
        val error = block()
        if (error != null) listOf(error) else emptyList()
    }
}

context(_: MediaLibraryDao)
internal suspend inline fun WrappedProducerScope<SyncStatus>.doRefreshIfNeeded(
    taskRefreshKey: TaskRefreshKey,
    force: Boolean,
    crossinline block: suspend () -> List<AppError>,
) {
    if (!force && !taskRefreshKey.needRefresh()) {
        Napier.d(tag = TAG) { "doRefreshIfNeeded: skip $taskRefreshKey" }
        return
    }

    Napier.d(tag = TAG) { "doRefreshIfNeeded: E $taskRefreshKey" }
    send(taskRefreshKey, SyncStatus.Loading)

    try {
        val errors = block()
        if (errors.isEmpty()) {
            taskRefreshKey.markAsRefreshed()
            send(taskRefreshKey, SyncStatus.Idle())
            Napier.d(tag = TAG) { "doRefreshIfNeeded finish success: $taskRefreshKey" }
        } else {
            send(taskRefreshKey, SyncStatus.Idle(errors))
            Napier.d(tag = TAG) { "doRefreshIfNeeded finish error: $errors $taskRefreshKey" }
        }
    } catch (_: CancellationException) {
        send(taskRefreshKey, SyncStatus.Idle())
    }
    Napier.d(tag = TAG) { "doRefreshIfNeeded: X $taskRefreshKey" }
}

@OptIn(ExperimentalTime::class)
context(mediaDao: MediaLibraryDao)
internal suspend fun TaskRefreshKey.needRefresh(): Boolean {
    val lastRefreshTimeStamp = mediaDao.getRefreshTimeStamp(key()) ?: return true

    val currentTime = Clock.System.now().toEpochMilliseconds()
    val refreshInterval = refreshIntervalMs()

    val elapsedTime = currentTime - lastRefreshTimeStamp

    return elapsedTime >= refreshInterval
}

@OptIn(ExperimentalTime::class)
context(mediaDao: MediaLibraryDao)
internal suspend fun TaskRefreshKey.markAsRefreshed() {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    mediaDao.upsertRefreshTimeStamp(key(), currentTime)
    Napier.d(tag = TAG) { "Marked as refreshed: ${key()} at $currentTime" }
}

internal fun TaskRefreshKey.refreshIntervalMs(): Long {
    fun hoursToMillis(hours: Long): Long = hours * 60 * 60 * 1000

    return when (this) {
        is TaskRefreshKey.SyncMediaListItem -> 0

        // Always refresh
        is TaskRefreshKey.AllCategories -> hoursToMillis(12)

        is TaskRefreshKey.SyncUserMediaList -> hoursToMillis(12)

        is TaskRefreshKey.SyncUserCondition -> hoursToMillis(1)

        is TaskRefreshKey.SyncDetailMediaItem -> hoursToMillis(1)

        is TaskRefreshKey.SyncDetailStaff -> hoursToMillis(1)

        is TaskRefreshKey.SyncDetailCharacter -> hoursToMillis(1)

        is TaskRefreshKey.SyncDetailStudio -> hoursToMillis(1)
    }
}
