package me.andannn.aniflow.service.dto

import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.aniflow.service.dto.enums.UserStaffNameLanguage
import me.andannn.aniflow.service.dto.enums.UserTitleLanguage

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
    public val avatar: UserAvatar?,
    /**
     * The user's banner images
     */
    public val bannerImage: String?,
    /**
     * The number of unread notifications the user has
     */
    public val unreadNotificationCount: Int?,
    /**
     * The user's general options
     */
    public val options: UserOptions?,
    /**
     * The user's media list options
     */
    public val mediaListOptions: MediaListOptions?,
)

public data class UserOptions(
    /**
     * The language the user wants to see media titles in
     */
    public val titleLanguage: UserTitleLanguage?,
    /**
     * Whether the user has enabled viewing of 18+ content
     */
    public val displayAdultContent: Boolean?,
    /**
     * Whether the user receives notifications when a show they are watching aires
     */
    public val airingNotifications: Boolean?,
    /**
     * Profile highlight color (blue, purple, pink, orange, red, green, gray)
     */
    public val profileColor: String?,
    /**
     * The user's timezone offset (Auth user only)
     */
    public val timezone: String?,
    /**
     * Minutes between activity for them to be merged together. 0 is Never, Above 2 weeks (20160
     * mins) is Always.
     */
    public val activityMergeTime: Int?,
    /**
     * The language the user wants to see staff and character names in
     */
    public val staffNameLanguage: UserStaffNameLanguage?,
    /**
     * Whether the user only allow messages from users they follow
     */
    public val restrictMessagesToFollowing: Boolean?,
)

public data class UserAvatar(
    /**
     * The avatar of user at its largest size
     */
    public val large: String?,
    /**
     * The avatar of user at medium size
     */
    public val medium: String?,
)

public data class MediaListOptions(
    /**
     * The score format the user is using for media lists
     */
    public val scoreFormat: ScoreFormat?,
)
