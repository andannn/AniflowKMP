/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val MEDIA_BASIC_INFO = $$"""
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
"""

fun buildMediaDetailQuerySchema(
    withCharacterConnection: Boolean = false,
    withStaffConnection: Boolean = false,
    withStudioConnection: Boolean = false,
    withRelations: Boolean = false,
): String =
    buildString {
        append($$"query ($id: Int")
        if (withCharacterConnection) append($$", $characterPage: Int, $characterPerPage: Int, $staffLanguage: StaffLanguage")
        if (withStaffConnection) append($$", $staffPage: Int, $staffPerPage: Int")
        append(") {")
        append(
            $$"""
            Media(id: $id) {
$$MEDIA_BASIC_INFO
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
                        $$CHARACTER_BASIC_INFO
                      }
                      voiceActors(language: $staffLanguage, sort: LANGUAGE) {
                        $$STAFF_BASIC_INFO
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
                        $$STAFF_BASIC_INFO
                      }
                    }
                  }
                """.trimIndent(),
            )
        }

        if (withStudioConnection) {
            append(
                """
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

        if (withRelations) {
            append(
                """
                relations {
                  edges {
                    relationType
                    node {
                      id
                      title {
                        romaji
                        english
                        native
                      }
                      type
                      format
                      status
                      coverImage {
                        large
                        medium
                        color
                      }
                      bannerImage
                      isFavourite
                      averageScore
                      meanScore
                      favourites
                      trending
                      nextAiringEpisode {
                        id
                        airingAt
                        episode
                        timeUntilAiring
                      }
                    }
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
