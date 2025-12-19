/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.usecase.data.sync

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.datastore.UserSettingPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

private const val TAG = "FetchNotificationTask"

data class NotificationResult(
    val notifications: List<NotificationModel>,
) {
    companion object {
        val Empty = NotificationResult(emptyList())
    }
}

class FetchNotificationTask :
    BackgroundSyncTask<NotificationResult>,
    KoinComponent {
    private val authRepository: AuthRepository by inject()
    private val mediaRepository: MediaRepository by inject()

    private val userSettingPreferences: UserSettingPreferences by inject()

    override suspend fun sync(): SyncResult<NotificationResult> {
        val authedUser =
            authRepository.getAuthedUserFlow().first() ?: run {
                Napier.d(tag = TAG) { "No auth return" }
                return SyncResult.Failure
            }

        authRepository.syncUserCondition()?.run {
            Napier.d(tag = TAG) { "Error syncing user condition: $this" }
            return SyncResult.Retry
        }

        val unreadNotificationCount = authedUser.unreadNotificationCount
        if (unreadNotificationCount == 0) {
            Napier.d(tag = TAG) { "No new notification" }
            // No notification, just finish the task.
            return SyncResult.Success(NotificationResult.Empty)
        }

        val (unReadNotificationPage, error) =
            mediaRepository.loadNotificationByPage(
                page = 1,
                perPage = unreadNotificationCount.coerceAtMost(25),
                category = NotificationCategory.ALL,
                resetNotificationCount = false,
            )

        if (error != null) {
            Napier.d(tag = TAG) { "Error loading notifications: $error" }
            return SyncResult.Retry
        }

        val alreadySentIds = userSettingPreferences.userData.first().sentNotificationIds

        val newNotifications =
            unReadNotificationPage.items.filter { it.id !in alreadySentIds }

        Napier.d(tag = TAG) { "sync success unReadNotificationPage ${unReadNotificationPage.items.size}" }

        userSettingPreferences.addSentNotificationId(
            newNotifications.map { it.id },
        )

        Napier.d(tag = TAG) { "sync success newNotifications ${newNotifications.size}" }
        return SyncResult.Success(NotificationResult(newNotifications))
    }
}
