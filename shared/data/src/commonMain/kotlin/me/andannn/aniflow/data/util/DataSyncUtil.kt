package me.andannn.aniflow.data.util

import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.internal.exceptions.toError

internal interface DataSyncer<T> {
    suspend fun getLocal(): T

    suspend fun saveLocal(data: T)

    suspend fun syncWithRemote(model: T)
}

internal suspend fun <T> DataSyncer<T>.postMutationAndRevertWhenException(modify: (T) -> T): AppError? {
    val oldModel = getLocal()
    val newModel = modify(oldModel)

    saveLocal(newModel)

    return try {
        syncWithRemote(newModel)
        saveLocal(newModel)
        null
    } catch (e: Throwable) {
        val error = e.toError()
        saveLocal(oldModel)
        error
    }
}
