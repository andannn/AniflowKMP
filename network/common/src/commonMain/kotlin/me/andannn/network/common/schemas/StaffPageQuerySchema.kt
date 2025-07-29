/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val STAFF_PAGE_QUERY_SCHEMA = $$"""
query ($id: Int, $page: Int, $perPage: Int) {
  Media(id: $id) {
    staff(page: $page, perPage: $perPage, sort: FAVOURITES_DESC) {
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
          name {
            first
            middle
            last
            full
            native
          }
          image {
            large
            medium
          }
        }
      }
    }
  }
}
    """
