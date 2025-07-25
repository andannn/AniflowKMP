package me.andannn.network.common.schemas

const val MEDIA_DETAIL_QUERY_SCHEMA = $$"""
query ($id: Int) {
  Media(id: $id) {
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
    favourites
    trending
    isFavourite
    nextAiringEpisode {
      id
      airingAt
      episode
      timeUntilAiring
    }
    characters(page: 1, perPage: 9, sort: RELEVANCE) {
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
        voiceActors(language: JAPANESE, sort: LANGUAGE) {
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
    staff(page: 1, perPage: 9, sort: FAVOURITES_DESC) {
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
    studios {
	  nodes {
	    id
        name
        isAnimationStudio
        siteUrl
        isFavourite
	  }
    }
  }
}
"""
