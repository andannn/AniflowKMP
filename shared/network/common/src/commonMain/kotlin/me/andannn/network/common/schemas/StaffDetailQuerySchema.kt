/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val STAFF_BASIC_INFO = $$"""
    id
    name {
      first
      middle
      last
      full
      native
      alternative
    }
    image {
      large
      medium
    }
    description(asHtml: true)
    gender
    dateOfBirth {
      year
      month
      day
    }
    dateOfDeath {
      year
      month
      day
    }
    age
    yearsActive
    homeTown
    bloodType
    isFavourite
    siteUrl
    """

const val STAFF_DETAIL_QUERY_SCHEMA = $$"""
query ($id: Int, $page: Int, $perPage: Int, $sort: [MediaSort]) {
  Staff(id: $id) {
    $$STAFF_BASIC_INFO
    characterMedia(sort: $sort, page: $page, perPage: $perPage) {
      pageInfo {
        total
        perPage
        currentPage
        lastPage
        hasNextPage
      }
      edges {
        id
        characters {
          id
          image {
            large
            medium
          }
          name {
            first
            middle
            last
            full
            native
          }
        }
        characterRole
        node {
          id
          type
          format
          status
          season
          seasonYear
          coverImage {
            extraLarge
            large
            medium
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
