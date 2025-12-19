/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

sealed class NotificationModel(
    open val id: String,
    open val context: String,
    open val createdAt: Int,
)

// -----------------------------
// 子类定义
// -----------------------------

data class AiringNotification(
    override val id: String,
    override val context: String,
    override val createdAt: Int,
    val episode: Int,
    val media: MediaModel,
) : NotificationModel(id, context, createdAt)

data class FollowNotification(
    override val id: String,
    override val context: String,
    override val createdAt: Int,
    val user: UserModel,
) : NotificationModel(id, context, createdAt)

sealed class ActivityNotification(
    override val id: String,
    override val context: String,
    override val createdAt: Int,
    open val user: UserModel,
    open val activityId: Int,
) : NotificationModel(id, context, createdAt) {
    data class Mention(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val user: UserModel,
        override val activityId: Int,
    ) : ActivityNotification(id, context, createdAt, user, activityId)

    data class Reply(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val user: UserModel,
        override val activityId: Int,
    ) : ActivityNotification(id, context, createdAt, user, activityId)

    data class ReplyLike(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val user: UserModel,
        override val activityId: Int,
    ) : ActivityNotification(id, context, createdAt, user, activityId)

    data class Like(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val user: UserModel,
        override val activityId: Int,
    ) : ActivityNotification(id, context, createdAt, user, activityId)

    data class Message(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val user: UserModel,
        override val activityId: Int,
    ) : ActivityNotification(id, context, createdAt, user, activityId)

    data class ReplySubscribed(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val user: UserModel,
        override val activityId: Int,
    ) : ActivityNotification(id, context, createdAt, user, activityId)
}

sealed class MediaNotification(
    override val id: String,
    override val context: String,
    override val createdAt: Int,
    open val media: MediaModel,
) : NotificationModel(id, context, createdAt) {
    data class RelatedMediaAddition(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val media: MediaModel,
    ) : MediaNotification(id, context, createdAt, media)

    data class MediaDataChange(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val media: MediaModel,
        val reason: String,
    ) : MediaNotification(id, context, createdAt, media)

    data class MediaMerge(
        override val id: String,
        override val context: String,
        override val createdAt: Int,
        override val media: MediaModel,
    ) : MediaNotification(id, context, createdAt, media)
}

data class MediaDeletion(
    override val id: String,
    override val context: String,
    override val createdAt: Int,
    val deletedMediaTitle: String,
    val reason: String,
) : NotificationModel(id, context, createdAt)
