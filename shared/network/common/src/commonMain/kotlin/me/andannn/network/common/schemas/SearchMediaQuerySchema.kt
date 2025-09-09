/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val SEARCH_MEDIA_QUERY_SCHEMA =
    $$"""
query ($page: Int, $perPage: Int, $search: String, $season: MediaSeason, $seasonYear: Int, $format_in: [MediaFormat], $type: MediaType, $isAdult: Boolean) {
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    media(type: $type, search: $search, season: $season, seasonYear: $seasonYear, format_in: $format_in, isAdult: $isAdult) {
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
    """
