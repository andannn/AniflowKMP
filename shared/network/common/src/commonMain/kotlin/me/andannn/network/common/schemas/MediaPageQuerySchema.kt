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
      title {
        romaji
        english
        native
      }
      type
      description(asHtml: false)
      episodes
      seasonYear
      season
      source
      genres
      status
      hashtag
      isFavourite
      externalLinks {
        id
        url
        site
        type
        siteId
        color
        icon
      }
      rankings {
        rank
        type
        allTime
      }
      trailer {
        id
        site
        thumbnail
      }
      coverImage {
        extraLarge
        large
        medium
        color
      }
      format
      bannerImage
      averageScore
      meanScore
      favourites
      trending
      isFavourite
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
