/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.model.ActivityNotification
import me.andannn.aniflow.data.model.AiringNotification
import me.andannn.aniflow.data.model.FollowNotification
import me.andannn.aniflow.data.model.MediaDeletion
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.MediaNotification
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.getUserTitleString
import me.andannn.aniflow.ui.DeepLinkHelper.NOTIFICATION_DOMAIN
import me.andannn.aniflow.usecase.data.sync.FetchNotificationTask
import me.andannn.aniflow.usecase.data.sync.SyncResult
import me.andannn.aniflow.util.Notification
import me.andannn.aniflow.util.NotificationChannel
import me.andannn.aniflow.util.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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
    private val authRepository: AuthRepository by inject()
    private val notificationHelper = NotificationHelper(appContext)

    override suspend fun doWork(): Result {
        Napier.d(tag = TAG) { "doWork E" }

        if (!notificationHelper.areNotificationsEnabled()) {
            Napier.d(tag = TAG) { "doWork Notification not enabled" }
            return Result.success()
        }

        val result = FetchNotificationTask().sync()

        if (result is SyncResult.Failure) {
            Napier.d(tag = TAG) { "doWork Failure" }
            return Result.failure()
        }

        if (result is SyncResult.Retry) {
            Napier.d(tag = TAG) { "doWork retry" }
            return Result.retry()
        }

        val options = authRepository.getUserOptionsFlow().first()
        val notifications =
            (result as SyncResult.Success).result.notifications.mapNotNull {
                it.toPlatformNotification(options.titleLanguage)
            }

        Napier.d(tag = TAG) { "doWork send notifications $notifications" }

        notifications.forEach { notification ->
            notificationHelper.sendNotification(notification)
        }

        Napier.d(tag = TAG) { "doWork success" }
        return Result.success()
    }
}

private fun NotificationModel.toPlatformNotification(titleLanguage: UserTitleLanguage): Notification? =
    when (this) {
        is AiringNotification -> {
            Notification(
                id = id.toInt(),
                title = "New media aired",
                body = createBodyText(titleLanguage),
                pendingIntentUri = SchemeUtil.createDeepLinkFromSiteUrl(media.siteUrl ?: ""),
                notificationChannel = NotificationChannel.Aired,
                coverUrl = media.coverImage,
            )
        }

        is FollowNotification -> {
            Notification(
                id = id.toInt(),
                title = "New follower",
                body = createBodyText(titleLanguage),
                pendingIntentUri = SchemeUtil.createDeepLinkFromSiteUrl(user.siteUrl ?: ""),
                notificationChannel = NotificationChannel.NewFollower,
                coverUrl = user.avatar,
            )
        }

        is ActivityNotification -> {
            Notification(
                id = id.toInt(),
                title = "New activity",
                body = createBodyText(titleLanguage),
                pendingIntentUri = "", // TODO implement later
                notificationChannel = NotificationChannel.Activity,
            )
        }

        is MediaNotification.MediaMerge,
        is MediaNotification.MediaDataChange,
        is MediaNotification.RelatedMediaAddition,
        -> {
            Notification(
                id = id.toInt(),
                title = "Media",
                body = createBodyText(titleLanguage),
                pendingIntentUri = SchemeUtil.createDeepLinkFromSiteUrl(this.media.siteUrl ?: ""),
                notificationChannel = NotificationChannel.Media,
                coverUrl = media.coverImage,
            )
        }

        is MediaDeletion -> {
            null
        }
    }

private object SchemeUtil {
    fun createDeepLinkFromSiteUrl(siteUrl: String): String {
        if (siteUrl.contains("https")) {
            return siteUrl.replaceFirst("https", NOTIFICATION_DOMAIN)
        }

        if (siteUrl.contains("http")) {
            return siteUrl.replaceFirst("http", NOTIFICATION_DOMAIN)
        }

        return siteUrl
    }
}

private fun NotificationModel.createBodyText(userTitleLanguage: UserTitleLanguage): String =
    when (this) {
        is AiringNotification -> {
            val parts: List<String> = Json.decodeFromString(context)
            val title = media.title.getUserTitleString(userTitleLanguage)
            buildString {
                append(parts.getOrNull(0).orEmpty())
                append(episode)
                append(parts.getOrNull(1).orEmpty())
                append(title)
                append(parts.getOrNull(2).orEmpty())
            }
        }

        is FollowNotification -> {
            buildString {
                append(user.name)
                append(context)
            }
        }

        is ActivityNotification -> {
            buildString {
                append(user.name)
                append(context)
            }
        }

        is MediaNotification -> {
            buildString {
                val title = media.title.getUserTitleString(userTitleLanguage)
                append(title)
                append(context)
            }
        }

        is MediaDeletion -> {
            TODO()
        }
    }

object MockNotificationTest {
    val MockAiringNotification =
        AiringNotification(
            id = "2",
            context = """["Episode "," of "," has aired."]""",
            createdAt = 0,
            episode = 1,
            media =
                MediaModel(
                    id = "2",
                    title =
                        Title(
                            romaji = "Shingeki no Kyojin",
                            english = "Attack on Titan",
                            native = "進撃の巨人",
                        ),
                    coverImage = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx184322-rRkaMQ7J1zOI.jpg",
                    siteUrl = "https://anilist.co/anime/1",
                    isFavourite = false,
                ),
        )

    suspend fun sendNotification(
        context: Context,
        notification: NotificationModel,
    ) {
        NotificationHelper(context).sendNotification(
            notification.toPlatformNotification(
                UserTitleLanguage.NATIVE,
            )!!,
        )
    }
}
