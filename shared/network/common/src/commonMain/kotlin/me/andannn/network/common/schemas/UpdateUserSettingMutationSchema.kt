/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val UPDATE_USER_SETTING_MUTATION_SCHEMA = $$"""
mutation UpdatedUserResponse($titleLanguage: UserTitleLanguage, $displayAdultContent: Boolean, $userStaffNameLanguage: UserStaffNameLanguage, $scoreFormat: ScoreFormat) {
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
