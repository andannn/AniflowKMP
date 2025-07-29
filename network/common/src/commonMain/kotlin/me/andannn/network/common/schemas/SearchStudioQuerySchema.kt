package me.andannn.network.common.schemas

const val SEARCH_STUDIO_QUERY_SCHEMA = $$"""
query (${'$'}page: Int,  ${'$'}search: String) {
  Page(page: ${'$'}page, perPage: 10) {
     pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    studios(search: ${'$'}search) {
      id
      name
      isAnimationStudio
      siteUrl
      isFavourite
    }
  }
}
"""
