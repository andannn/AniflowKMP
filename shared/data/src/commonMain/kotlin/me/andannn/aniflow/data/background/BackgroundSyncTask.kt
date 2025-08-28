package me.andannn.aniflow.data.background

/**
 * A background sync task that can be scheduled to run in the background
 */
interface BackgroundSyncTask<T> {
    suspend fun sync(): SyncResult<T>
}

/**
 * The result of a sync operation
 */
sealed interface SyncResult<out T> {
    /**
     * The sync operation was successful
     */
    data class Success<T>(
        val result: T,
    ) : SyncResult<T>

    /**
     * The sync operation failed
     */
    data object Failure : SyncResult<Nothing>

    /**
     * The sync operation should be retried later
     */
    data object Retry : SyncResult<Nothing>
}
