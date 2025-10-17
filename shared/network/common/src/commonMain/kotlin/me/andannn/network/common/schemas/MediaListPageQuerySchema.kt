/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val MEDIA_LIST_PAGE_QUERY_SCHEMA = $$"""
query ($page: Int, $perPage: Int, $userId: Int, $status_in: [MediaListStatus], $type: MediaType, $format: ScoreFormat, $mediaListSort: [MediaListSort]) {
  Page(page: $page, perPage: $perPage) {
    pageInfo {
      total
      perPage
      currentPage
      lastPage
      hasNextPage
    }
    mediaList(userId: $userId, type: $type, status_in: $status_in, sort: $mediaListSort) {
      id
      status
      progress
      priority
      notes
      repeat
      private
      userId
      updatedAt
      score(format: $format)
      progressVolumes
      startedAt {
        year
        month
        day
      }
      completedAt {
        year
        month
        day
      }
      media {
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
        meanScore
        favourites
        trending
        isFavourite
        nextAiringEpisode {
          id
          airingAt
          episode
          timeUntilAiring
        }
      }
    }
  }
}
    """
