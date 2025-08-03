/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.andannn.aniflow.service.dto.enums.NotificationType

@Serializable(with = NotificationUnionSerializer::class)
sealed interface NotificationUnion

@Serializable
public data class AiringNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the aired anime
     */
    public val animeId: Int,
    /**
     * The episode number that just aired
     */
    public val episode: Int,
    /**
     * The notification context text
     */
    public val contexts: List<String?>? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The associated media of the airing schedule
     */
    public val media: Media? = null,
) : NotificationUnion

@Serializable
public data class ActivityLikeNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The id of the user who liked to the activity
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The id of the activity which was liked
     */
    public val activityId: Int,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The user who liked the activity
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class ActivityMentionNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The id of the user who mentioned the authenticated user
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the activity where mentioned
     */
    public val activityId: Int,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The user who mentioned the authenticated user
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class ActivityMessageNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The if of the user who send the message
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the activity message
     */
    public val activityId: Int,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The message activity
     */
    public val message: MessageActivity? = null,
    /**
     * The user who sent the message
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class ActivityReplySubscribedNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The id of the user who replied to the activity
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the activity which was replied too
     */
    public val activityId: Int,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The user who replied to the activity
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class ActivityReplyLikeNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The id of the user who liked to the activity reply
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the activity where the reply which was liked
     */
    public val activityId: Int,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The user who liked the activity reply
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class ActivityReplyNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The id of the user who replied to the activity
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the activity which was replied too
     */
    public val activityId: Int,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The user who replied to the activity
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class FollowingNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The id of the user who followed the authenticated user
     */
    public val userId: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The liked activity
     */
    public val user: User? = null,
) : NotificationUnion

@Serializable
public data class MediaMergeNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the media that was merged into
     */
    public val mediaId: Int,
    /**
     * The title of the deleted media
     */
    public val deletedMediaTitles: List<String?>? = null,
    /**
     * The reason for the media data change
     */
    public val context: String? = null,
    /**
     * The reason for the media merge
     */
    public val reason: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The media that was merged into
     */
    public val media: Media? = null,
) : NotificationUnion

@Serializable
public data class MediaDataChangeNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the media that received data changes
     */
    public val mediaId: Int,
    /**
     * The reason for the media data change
     */
    public val context: String? = null,
    /**
     * The reason for the media data change
     */
    public val reason: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The media that received data changes
     */
    public val media: Media? = null,
) : NotificationUnion

@Serializable
public data class MediaDeletionNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The title of the deleted media
     */
    public val deletedMediaTitle: String? = null,
    /**
     * The reason for the media deletion
     */
    public val context: String? = null,
    /**
     * The reason for the media deletion
     */
    public val reason: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
) : NotificationUnion

@Serializable
public data class RelatedMediaAdditionNotification(
    /**
     * The id of the Notification
     */
    public val id: Int,
    /**
     * The type of notification
     */
    public val type: NotificationType? = null,
    /**
     * The id of the new media
     */
    public val mediaId: Int,
    /**
     * The notification context text
     */
    public val context: String? = null,
    /**
     * The time the notification was created at
     */
    public val createdAt: Int? = null,
    /**
     * The associated media of the airing schedule
     */
    public val media: Media? = null,
) : NotificationUnion

private object NotificationUnionSerializer : KSerializer<NotificationUnion> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("NotificationUnion") {
        }

    override fun deserialize(decoder: Decoder): NotificationUnion {
        val jsonDecoder =
            decoder as? kotlinx.serialization.json.JsonDecoder
                ?: throw kotlinx.serialization.SerializationException("This class can only be loaded using JSON")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return when (val type = jsonObject["__typename"]?.jsonPrimitive?.contentOrNull) {
            "AiringNotification" -> jsonDecoder.json.decodeFromJsonElement<AiringNotification>(jsonObject)
            "ActivityLikeNotification" -> jsonDecoder.json.decodeFromJsonElement<ActivityLikeNotification>(jsonObject)
            "ActivityMentionNotification" -> jsonDecoder.json.decodeFromJsonElement<ActivityMentionNotification>(jsonObject)
            "ActivityMessageNotification" -> jsonDecoder.json.decodeFromJsonElement<ActivityMessageNotification>(jsonObject)
            "ActivityReplySubscribedNotification" -> jsonDecoder.json.decodeFromJsonElement<ActivityReplySubscribedNotification>(jsonObject)
            "ActivityReplyLikeNotification" -> jsonDecoder.json.decodeFromJsonElement<ActivityReplyLikeNotification>(jsonObject)
            "ActivityReplyNotification" -> jsonDecoder.json.decodeFromJsonElement<ActivityReplyNotification>(jsonObject)
            "FollowingNotification" -> jsonDecoder.json.decodeFromJsonElement<FollowingNotification>(jsonObject)
            "MediaMergeNotification" -> jsonDecoder.json.decodeFromJsonElement<MediaMergeNotification>(jsonObject)
            "MediaDataChangeNotification" -> jsonDecoder.json.decodeFromJsonElement<MediaDataChangeNotification>(jsonObject)
            "MediaDeletionNotification" -> jsonDecoder.json.decodeFromJsonElement<MediaDeletionNotification>(jsonObject)
            "RelatedMediaAdditionNotification" -> jsonDecoder.json.decodeFromJsonElement<RelatedMediaAdditionNotification>(jsonObject)

            else -> throw kotlinx.serialization.SerializationException("Unknown activity type: $type")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: NotificationUnion,
    ): Unit = throw NotImplementedError("Serialization not implemented")
}
