/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val ACTIVITY_PAGE_QUERY_SCHEMA = $$"""
query ($page: Int, $perPage: Int, $userId: Int, $type_in: [ActivityType], $mediaId: Int, $isFollowing: Boolean, $hasRepliesOrTypeText: Boolean) {
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    activities(userId: $userId, type_in: $type_in, mediaId: $mediaId, isFollowing: $isFollowing, sort: ID_DESC, hasRepliesOrTypeText: $hasRepliesOrTypeText) {
      __typename
      ... on TextActivity {
        id
        text(asHtml: true)
        userId
        type
        replyCount
        siteUrl
        isLocked
        isLiked
        likeCount
        isPinned
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
      ... on ListActivity {
        id
        status
        progress
        userId
        type
        replyCount
        siteUrl
        isLocked
        isLiked
        likeCount
        isPinned
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
        media {
          id
          type
          format
          status
          season
          seasonYear
          startDate {
            year
            month
            day
          }
          coverImage {
            extraLarge
            large
            medium
            color
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
