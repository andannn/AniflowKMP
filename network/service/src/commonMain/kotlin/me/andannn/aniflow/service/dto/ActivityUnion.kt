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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.andannn.aniflow.service.dto.enums.ActivityType

@Serializable(with = ActivitySerializer::class)
sealed interface ActivityUnion

@Serializable
public data class TextActivity(
    /**
     * The id of the activity
     */
    public val id: Int,
    /**
     * The status text (Markdown)
     */
    public val text: String? = null,
    /**
     * The user id of the activity's creator
     */
    public val userId: Int? = null,
    /**
     * The type of activity
     */
    public val type: ActivityType? = null,
    /**
     * The number of activity replies
     */
    public val replyCount: Int,
    /**
     * The url for the activity page on the AniList website
     */
    public val siteUrl: String? = null,
    /**
     * If the activity is locked and can receive replies
     */
    public val isLocked: Boolean? = null,
    /**
     * If the currently authenticated user liked the activity
     */
    public val isLiked: Boolean? = null,
    /**
     * The amount of likes the activity has
     */
    public val likeCount: Int,
    /**
     * If the activity is pinned to the top of the users activity feed
     */
    public val isPinned: Boolean? = null,
    /**
     * The time the activity was created at
     */
    public val createdAt: Int,
    /**
     * The user who created the activity
     */
    public val user: User? = null,
) : ActivityUnion

@Serializable
public data class ListActivity(
    /**
     * The id of the activity
     */
    public val id: Int,
    /**
     * The list item's textual status
     */
    public val status: String? = null,
    /**
     * The list progress made
     */
    public val progress: String? = null,
    /**
     * The user id of the activity's creator
     */
    public val userId: Int? = null,
    /**
     * The type of activity
     */
    public val type: ActivityType? = null,
    /**
     * The number of activity replies
     */
    public val replyCount: Int,
    /**
     * The url for the activity page on the AniList website
     */
    public val siteUrl: String? = null,
    /**
     * If the activity is locked and can receive replies
     */
    public val isLocked: Boolean? = null,
    /**
     * If the currently authenticated user liked the activity
     */
    public val isLiked: Boolean? = null,
    /**
     * The amount of likes the activity has
     */
    public val likeCount: Int,
    /**
     * If the activity is pinned to the top of the users activity feed
     */
    public val isPinned: Boolean? = null,
    /**
     * The time the activity was created at
     */
    public val createdAt: Int,
    /**
     * The owner of the activity
     */
    public val user: User? = null,
    /**
     * The associated media to the activity update
     */
    public val media: Media? = null,
) : ActivityUnion

@Serializable
public data class MessageActivity(
    /**
     * The id of the activity
     */
    public val id: Int,
)

private object ActivitySerializer : KSerializer<ActivityUnion> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("activity") {
        }

    override fun deserialize(decoder: Decoder): ActivityUnion {
        val jsonDecoder =
            decoder as? kotlinx.serialization.json.JsonDecoder
                ?: throw kotlinx.serialization.SerializationException("This class can only be loaded using JSON")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        return when (val type = jsonObject["__typename"]?.jsonPrimitive?.contentOrNull) {
            "TextActivity" -> jsonDecoder.json.decodeFromJsonElement(TextActivity.serializer(), jsonObject)
            "ListActivity" -> jsonDecoder.json.decodeFromJsonElement(ListActivity.serializer(), jsonObject)
            else -> throw kotlinx.serialization.SerializationException("Unknown activity type: $type")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: ActivityUnion,
    ): Unit = throw NotImplementedError("Serialization not implemented")
}
