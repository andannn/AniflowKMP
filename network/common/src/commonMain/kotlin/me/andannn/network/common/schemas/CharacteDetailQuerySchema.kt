/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val CHARACTER_DETAIL_QUERY_SCHEMA = $$"""
query ($id: Int) {
  Character(id: $id) {
    id
    name {
      first
      middle
      last
      full
      native
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
    media {
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
