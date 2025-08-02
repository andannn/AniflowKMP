/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val STUDIO_DETAIL_QUERY_SCHEMA = $$"""
query ($id: Int, $page: Int, $perPage: Int) {
  Studio(id: $id) {
    id
    name
    isAnimationStudio
    siteUrl
    isFavourite
    media(page: $page, perPage: $perPage, sort: [START_DATE_DESC]) {
      pageInfo {
        total
        perPage
        currentPage
        lastPage
        hasNextPage
      }
      edges {
        relationType
        node {
          id
          type
          format
          status
          episodes
          seasonYear
          season
          source
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
          startDate {
            year
            month
            day
          }
          endDate {
            year
            month
            day
          }
        }
      }
    }
  }
}
"""
