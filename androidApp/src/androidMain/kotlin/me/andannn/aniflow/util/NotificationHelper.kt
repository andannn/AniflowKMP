/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import me.andannn.aniflow.MainActivity

data class Notification(
    val id: Int,
    val title: String,
    val body: String,
    val notificationChannel: NotificationChannel,
    val pendingIntentUri: String,
)

class NotificationHelper(
    private val context: Context,
) {
    fun areNotificationsEnabled(): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()

    fun sendNotification(notificationModel: Notification) {
        // create notification channel.
        val notificationManager = NotificationManagerCompat.from(context)
        val notificationChannel = notificationModel.notificationChannel
        val channel =
            NotificationChannelCompat
                .Builder(
                    notificationChannel.id,
                    notificationChannel.importance,
                ).setName(notificationChannel.name)
                .setDescription(notificationChannel.description)
                .build()
        notificationManager.createNotificationChannel(channel)

        val intent =
            Intent(
                Intent.ACTION_VIEW,
                notificationModel.pendingIntentUri.toUri(),
                context,
                MainActivity::class.java,
            )
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // send notification.
        val notification =
            NotificationCompat
                .Builder(
                    context,
                    notificationChannel.id,
                ).setContentTitle(notificationModel.title)
                .setContentText(notificationModel.body)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .build()
        notificationManager.notify(notificationModel.id, notification)
    }

    fun isNotificationChannelEnabled(channel: NotificationChannel): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)

        val channel = notificationManager.getNotificationChannel(channel.id)
        if (channel == null) {
            // channel is not been created yet.
            return true
        }

        return channel.importance != NotificationManagerCompat.IMPORTANCE_NONE
    }
}

sealed class NotificationChannel {
    abstract val id: String
    abstract val name: String
    abstract val description: String
    abstract val importance: Int

    data object Aired : NotificationChannel() {
        override val id: String = "MediaUpdateNotificationChannel"
        override val name: String = "Airing"
        override val description: String = "Media contents update"
        override val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    }

    data object NewFollower : NotificationChannel() {
        override val id: String = "NewFollowerNotificationChannel"
        override val name: String = "Followers"
        override val description: String = "New follower"
        override val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    }

    data object Activity : NotificationChannel() {
        override val id: String = "ActivityNotificationChannel"
        override val name: String = "Activity"
        override val description: String = "Activity related"
        override val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    }

    data object Media : NotificationChannel() {
        override val id: String = "MediaNotificationChannel"
        override val name: String = "Media"
        override val description: String = "Media related"
        override val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    }
}
