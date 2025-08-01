/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val SEARCH_STAFF_QUERY_SCHEMA =
    $$"""
query ($page: Int, $perPage: Int, $search: String) {
  page: Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    staff(search: $search) {
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
    """
