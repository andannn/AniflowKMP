/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.tasks

import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.SyncStatus
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.database.MediaLibraryDao
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal const val TAG = "SideEffectTask"

internal fun createSideEffectFlow(forceRefreshFirstTime: Boolean, vararg tasks: SideEffectTask<SyncStatus>) =
    channelFlow {
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
        is TaskRefreshKey.AllCategories -> hoursToMillis(12)
        is TaskRefreshKey.SyncUserMediaList -> hoursToMillis(12)
    }
}
