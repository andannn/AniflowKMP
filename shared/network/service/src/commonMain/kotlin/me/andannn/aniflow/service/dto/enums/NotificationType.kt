/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

/**
 * Notification type enum
 */
enum class NotificationType(
    public val rawValue: String,
) {
    /**
     * A user has sent you message
     */
    ACTIVITY_MESSAGE("ACTIVITY_MESSAGE"),

    /**
     * A user has replied to your activity
     */
    ACTIVITY_REPLY("ACTIVITY_REPLY"),

    /**
     * A user has followed you
     */
    FOLLOWING("FOLLOWING"),

    /**
     * A user has mentioned you in their activity
     */
    ACTIVITY_MENTION("ACTIVITY_MENTION"),

    /**
     * A user has mentioned you in a forum comment
     */
    THREAD_COMMENT_MENTION("THREAD_COMMENT_MENTION"),

    /**
     * A user has commented in one of your subscribed forum threads
     */
    THREAD_SUBSCRIBED("THREAD_SUBSCRIBED"),

    /**
     * A user has replied to your forum comment
     */
    THREAD_COMMENT_REPLY("THREAD_COMMENT_REPLY"),

    /**
     * An anime you are currently watching has aired
     */
    AIRING("AIRING"),

    /**
     * A user has liked your activity
     */
    ACTIVITY_LIKE("ACTIVITY_LIKE"),

    /**
     * A user has liked your activity reply
     */
    ACTIVITY_REPLY_LIKE("ACTIVITY_REPLY_LIKE"),

    /**
     * A user has liked your forum thread
     */
    THREAD_LIKE("THREAD_LIKE"),

    /**
     * A user has liked your forum comment
     */
    THREAD_COMMENT_LIKE("THREAD_COMMENT_LIKE"),

    /**
     * A user has replied to activity you have also replied to
     */
    ACTIVITY_REPLY_SUBSCRIBED("ACTIVITY_REPLY_SUBSCRIBED"),

    /**
     * A new anime or manga has been added to the site where its related media is on the user's list
     */
    RELATED_MEDIA_ADDITION("RELATED_MEDIA_ADDITION"),

    /**
     * An anime or manga has had a data change that affects how a user may track it in their lists
     */
    MEDIA_DATA_CHANGE("MEDIA_DATA_CHANGE"),

    /**
     * Anime or manga entries on the user's list have been merged into a single entry
     */
    MEDIA_MERGE("MEDIA_MERGE"),

    /**
     * An anime or manga on the user's list has been deleted from the site
     */
    MEDIA_DELETION("MEDIA_DELETION"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
