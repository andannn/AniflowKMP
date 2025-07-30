/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

fun buildMediaDetailQuerySchema(
    withCharacterConnection: Boolean = false,
    withStaffConnection: Boolean = false,
    withStudioConnection: Boolean = false,
): String =
    buildString {
        append($$"query ($id: Int")
        if (withCharacterConnection) append($$", $characterPage: Int, $characterPerPage: Int")
        if (withStaffConnection) append($$", $staffPage: Int, $staffPerPage: Int, $staffLanguage: StaffLanguage")
        append(") {")
        append(
            $$"""
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
            """,
        )
        if (withCharacterConnection) {
            append(
                $$"""
                characters(page: $characterPage, perPage: $characterPerPage, sort: RELEVANCE) {
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
                """.trimIndent(),
            )
        }

        if (withStaffConnection) {
            append(
                $$"""
                staff(page: $staffPage, perPage: $staffPerPage, sort: FAVOURITES_DESC) {
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
                """.trimIndent(),
            )
        }

        if (withStudioConnection) {
            append(
                $$"""
                studios {
                  nodes {
                    id
                    name
                    isAnimationStudio
                    siteUrl
                    isFavourite
                  }
                }
                """.trimIndent(),
            )
        }

        append(
            """
              }
            }
            """.trimIndent(),
        )
    }
