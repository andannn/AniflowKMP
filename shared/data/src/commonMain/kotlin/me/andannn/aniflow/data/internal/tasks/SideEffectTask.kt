/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.internal.tasks

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.SyncStatus

internal fun createSideEffectFlow(vararg tasks: SideEffectTask<SyncStatus>) =
    channelFlow {
        supervisorScope {
            tasks.forEach { task ->
                launch {
                    with(WrappedProducerScope(this@channelFlow)) {
                        with(task) {
                            run()
                        }
                    }
                }
            }
        }
    }.aggregateStatus()

internal interface SideEffectTask<T> {
    val tag: String

    suspend fun WrappedProducerScope<T>.run()
}

internal class WrappedProducerScope<T>(
    internal val producerScope: ProducerScope<DataWithKey<T>>,
) {
    suspend fun SideEffectTask<*>.send(value: T) {
        producerScope.send(DataWithKey(tag, value))
    }
}

internal data class DataWithKey<T>(
    val key: String,
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
