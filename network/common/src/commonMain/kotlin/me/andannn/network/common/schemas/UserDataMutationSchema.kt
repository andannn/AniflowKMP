package me.andannn.network.common.schemas

const val USER_DATA_MUTATION_SCHEMA = $$"""
mutation GetUserData($titleLanguage: UserTitleLanguage, $displayAdultContent: Boolean, $userStaffNameLanguage: UserStaffNameLanguage, $scoreFormat: ScoreFormat) {
    UpdateUser(titleLanguage: $titleLanguage, displayAdultContent: $displayAdultContent, staffNameLanguage: $userStaffNameLanguage, scoreFormat: $scoreFormat) {
        id
        name
        avatar {
            large
            medium
        }
        bannerImage
        unreadNotificationCount
        options {
            titleLanguage
            displayAdultContent
            airingNotifications
            profileColor
            timezone
            activityMergeTime
            staffNameLanguage
            restrictMessagesToFollowing
        }
        mediaListOptions {
            scoreFormat
        }
    }
}
"""
