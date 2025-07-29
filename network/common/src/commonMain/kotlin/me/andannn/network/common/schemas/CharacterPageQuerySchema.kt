/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val CHARACTER_PAGE_QUERY_SCHEMA =
    $$"""
query ($id: Int, $page: Int, $perPage: Int, $staffLanguage: StaffLanguage) {
  Media(id: $id) {
    characters(page: $page, perPage: $perPage, sort: RELEVANCE) {
      pageInfo {
        total
        perPage
        currentPage
        lastPage
        hasNextPage
      }
      edges {
        role
        node {
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
        voiceActors(language: $staffLanguage, sort: LANGUAGE) {
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
      }
    }
  }
}
"""
