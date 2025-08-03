/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val SEARCH_MEDIA_QUERY_SCHEMA =
    $$"""
query ($page: Int, $perPage: Int, $search: String, $type: MediaType, $isAdult: Boolean) {
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    media(type: $type, search: $search, isAdult: $isAdult) {
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
