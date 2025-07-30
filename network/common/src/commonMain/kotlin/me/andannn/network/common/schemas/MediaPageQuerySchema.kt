/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val MEDIA_PAGE_QUERY_SCHEMA =
    $$"""
query ($page: Int, $perPage: Int, $type: MediaType, $countryCode: CountryCode, $seasonYear: Int, $season: MediaSeason, $status: MediaStatus, $sort: [MediaSort], $format_in: [MediaFormat], $isAdult: Boolean, $startDate_greater: FuzzyDateInt, $endDate_lesser: FuzzyDateInt) {
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    media: media(type: $type, countryOfOrigin: $countryCode, seasonYear: $seasonYear, season: $season, status: $status, sort: $sort, format_in: $format_in, isAdult: $isAdult, startDate_greater: $startDate_greater, endDate_lesser: $endDate_lesser) {
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
