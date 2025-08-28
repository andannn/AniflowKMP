package me.andannn.aniflow.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.background.FetchNotificationTask
import me.andannn.aniflow.data.background.SyncResult
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

object SyncWorkHelper {
    private const val PERIODIC_SYNC_WORK_NAME = "periodic_sync_work_name"

    // For testing purpose
    fun doOneTimeSyncWork(context: Context) {
        val workManager = WorkManager.getInstance(context = context)
        val oneTimeWorkRequest =
            androidx.work
                .OneTimeWorkRequestBuilder<PostNotificationWorker>()
                .build()
        workManager.enqueue(oneTimeWorkRequest)
    }

    fun registerPeriodicSyncWork(context: Context) {
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<PostNotificationWorker>(1, TimeUnit.HOURS)
                .build()

        val workManager = WorkManager.getInstance(context = context)
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest,
        )
    }
}

private const val TAG = "PostNotificationWorker"

class PostNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params),
    KoinComponent {
    override suspend fun doWork(): Result {
        Napier.d(tag = TAG) { "doWork E" }

        val result = FetchNotificationTask().sync()

        if (result is SyncResult.Failure) {
            Napier.d(tag = TAG) { "doWork Failure" }
            return Result.failure()
        }

        if (result is SyncResult.Retry) {
            Napier.d(tag = TAG) { "doWork retry" }
            return Result.retry()
        }

        val notifications = (result as SyncResult.Success).result.notifications

        Napier.d(tag = TAG) { "doWork success" }
        return Result.success()
    }
}
