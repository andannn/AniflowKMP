/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val MEDIA_LIST_PAGE_QUERY_SCHEMA = $$"""
query($page: Int, $perPage: Int, $userId: Int, $status_in: [MediaListStatus], $type: MediaType, $format: ScoreFormat){
  Page(page: $page, perPage: $perPage) {
    pageInfo {
        total
        perPage
        currentPage
        lastPage
        hasNextPage
    }
    mediaList(userId: $userId, type: $type, status_in: $status_in) {
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
}
    """
