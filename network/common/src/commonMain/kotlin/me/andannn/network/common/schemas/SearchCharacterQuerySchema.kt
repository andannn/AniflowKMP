package me.andannn.network.common.schemas

const val SEARCH_CHARACTER_QUERY_SCHEMA = $$"""
query ($page: Int, $perPage: Int, $search: String) {
  Page(page: $page, perPage: $perPage) {
      pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    characters(search: $search) {
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
"""
