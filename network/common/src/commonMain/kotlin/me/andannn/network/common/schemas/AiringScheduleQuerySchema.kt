/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val AIRING_SCHEDULE_QUERY_SCHEMA =
    $$"""
query($page: Int, $perPage: Int, $airingAt_greater: Int, $airingAt_lesser: Int){
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    airingSchedules(airingAt_greater: $airingAt_greater, airingAt_lesser: $airingAt_lesser) {
      id
      airingAt
      timeUntilAiring
      episode
      mediaId
      media {
        id
        type
        format
        status
        seasonYear
        season
        source
        episodes
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
    """
