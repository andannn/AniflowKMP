/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val CHARACTER_DETAIL_QUERY_SCHEMA = $$"""
query ($id: Int, $page: Int, $perPage: Int, $mediaSort: [MediaSort]) {
  Character(id: $id) {
    id
    name {
      first
      middle
      last
      full
      native,
      alternative
    }
    image {
      medium
      large
    }
    description(asHtml: true)
    gender
    dateOfBirth {
      year
      month
      day
    }
    age
    bloodType
    isFavourite
    siteUrl
    favourites
    media(page: $page, perPage: $perPage, sort: $mediaSort) {
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
