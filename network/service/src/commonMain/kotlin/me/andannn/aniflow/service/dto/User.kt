package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage
import me.andannn.aniflow.service.dto.enums.UserTitleLanguage

@Serializable
public data class User(
    /**
     * The id of the user
     */
    public val id: Int,
    /**
     * The name of the user
     */
    public val name: String,
    /**
     * The user's avatar images
     */
    public val avatar: UserAvatar? = null,
    /**
     * The user's banner images
     */
    public val bannerImage: String? = null,
    /**
     * The number of unread notifications the user has
     */
    public val unreadNotificationCount: Int? = null,
    /**
     * The user's general options
     */
    public val options: UserOptions? = null,
    /**
     * The user's media list options
     */
    public val mediaListOptions: MediaListOptions? = null,
)

@Serializable
public data class UserOptions(
    /**
     * The language the user wants to see media titles in
     */
    public val titleLanguage: UserTitleLanguage? = null,
    /**
     * Whether the user has enabled viewing of 18+ content
     */
    public val displayAdultContent: Boolean? = null,
    /**
     * Whether the user receives notifications when a show they are watching aires
     */
    public val airingNotifications: Boolean? = null,
    /**
     * Profile highlight color (blue, purple, pink, orange, red, green, gray)
     */
    public val profileColor: String? = null,
    /**
     * The user's timezone offset (Auth user only)
     */
    public val timezone: String? = null,
    /**
     * Minutes between activity for them to be merged together. 0 is Never, Above 2 weeks (20160
     * mins) is Always.
     */
    public val activityMergeTime: Int? = null,
    /**
     * The language the user wants to see staff and character names in
     */
    public val staffNameLanguage: UserStaffNameLanguage? = null,
    /**
     * Whether the user only allow messages from users they follow
     */
    public val restrictMessagesToFollowing: Boolean? = null,
)

@Serializable
public data class UserAvatar(
    /**
     * The avatar of user at its largest size
     */
    public val large: String? = null,
    /**
     * The avatar of user at medium size
     */
    public val medium: String? = null,
)

@Serializable
public data class MediaListOptions(
    /**
     * The score format the user is using for media lists
     */
    public val scoreFormat: ScoreFormat? = null,
)
