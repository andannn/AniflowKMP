/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val MEDIA_LIST_QUERY_SCHEMA = $$"""
query($mediaId: Int, $userId: Int, $format: ScoreFormat) {
  MediaList(mediaId: $mediaId, userId: $userId) {
      id
      status
      progress
      priority
      notes
      repeat
      private
      userId
      updatedAt
      score(format: $format)
      progressVolumes
      startedAt {
        year
        month
        day
      }
      completedAt {
        year
        month
        day
      }
      media {
        id
        title {
          romaji
          english
          native
        }
        type
        format
        description(asHtml: false)
        episodes
        seasonYear
        season
        source
        status
        coverImage {
          extraLarge
          large
          medium
          color
        }
        averageScore
        favourites
        trending
        nextAiringEpisode {
          id
          airingAt
          episode
          timeUntilAiring
        }
      }
  }
}
"""
