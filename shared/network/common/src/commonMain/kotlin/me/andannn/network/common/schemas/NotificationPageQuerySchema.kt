/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val NOTIFICATION_PAGE_QUERY_SCHEMA =
    $$"""
query ($page: Int, $perPage: Int, $type_in: [NotificationType], $resetNotificationCount: Boolean) {
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    notifications(type_in: $type_in, resetNotificationCount: $resetNotificationCount) {
      __typename
      ... on AiringNotification {
        id
        type
        animeId
        episode
        contexts
        createdAt
        media {
          id
          siteUrl
          coverImage {
            extraLarge
            large
            medium
          }
          title {
            romaji
            english
            native
          }
        }
      }
      ... on ActivityLikeNotification {
        id
        userId
        type
        context
        activityId
        createdAt
        user {
          id
          name
          siteUrl
          avatar {
            large
          }
          bannerImage
          options {
            profileColor
          }
        }
      }
      ... on ActivityMentionNotification {
        id
        userId
        type
        activityId
        context
        createdAt
        user {
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
      ... on ActivityMessageNotification {
        id
        userId
        type
        activityId
        context
        createdAt
        message {
          id
        }
        user {
          id
          name
          siteUrl
          avatar {
            large
          }
          bannerImage
          options {
            profileColor
          }
        }
      }
      ... on ActivityReplySubscribedNotification {
        id
        userId
        type
        activityId
        context
        createdAt
        user {
          id
          name
          siteUrl
          avatar {
            large
          }
          bannerImage
          options {
            profileColor
          }
        }
      }
      ... on ActivityReplyLikeNotification {
        id
        userId
        type
        activityId
        context
        createdAt
        user {
          id
          name
          siteUrl
          avatar {
            large
          }
          bannerImage
          options {
            profileColor
          }
        }
      }
      ... on ActivityReplyNotification {
        id
        userId
        type
        activityId
        context
        createdAt
        user {
          id
          name
          siteUrl
          avatar {
            large
          }
          bannerImage
          options {
            profileColor
          }
        }
      }
      ... on FollowingNotification {
        id
        userId
        type
        context
        createdAt
        user {
          id
          name
          siteUrl
          avatar {
            large
          }
          bannerImage
          options {
            profileColor
          }
        }
      }
      ... on MediaMergeNotification {
        id
        type
        mediaId
        deletedMediaTitles
        context
        reason
        createdAt
        media {
          id
          siteUrl
          coverImage {
            extraLarge
            large
            medium
          }
          title {
            romaji
            english
            native
          }
        }
      }
      ... on MediaDataChangeNotification {
        id
        type
        mediaId
        context
        reason
        createdAt
        media {
          id
          siteUrl
          coverImage {
            extraLarge
            large
            medium
          }
          title {
            romaji
            english
            native
          }
        }
      }
      ... on MediaDeletionNotification {
        id
        type
        deletedMediaTitle
        context
        reason
        createdAt
      }
      ... on RelatedMediaAdditionNotification {
        id
        type
        mediaId
        context
        createdAt
        media {
          id
          siteUrl
          coverImage {
            extraLarge
            large
            medium
          }
          title {
            romaji
            english
            native
          }
        }
      }
    }
  }
}
"""
