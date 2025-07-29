/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json
import me.andannn.network.common.GraphQLBody
import me.andannn.network.common.schemas.ACTIVITY_PAGE_QUERY_SCHEMA
import me.andannn.network.common.schemas.AIRING_SCHEDULE_QUERY_SCHEMA
import me.andannn.network.common.schemas.CHARACTER_DETAIL_QUERY_SCHEMA
import me.andannn.network.common.schemas.CHARACTER_PAGE_QUERY_SCHEMA
import me.andannn.network.common.schemas.MEDIA_DETAIL_QUERY_SCHEMA
import me.andannn.network.common.schemas.MEDIA_LIST_PAGE_QUERY_SCHEMA
import me.andannn.network.common.schemas.MEDIA_LIST_QUERY_SCHEMA
import me.andannn.network.common.schemas.MEDIA_PAGE_QUERY_SCHEMA
import me.andannn.network.common.schemas.SEARCH_CHARACTER_QUERY_SCHEMA
import me.andannn.network.common.schemas.SEARCH_MEDIA_QUERY_SCHEMA
import me.andannn.network.common.schemas.SEARCH_STAFF_QUERY_SCHEMA
import me.andannn.network.common.schemas.SEARCH_STUDIO_QUERY_SCHEMA
import me.andannn.network.common.schemas.STAFF_DETAIL_QUERY_SCHEMA
import me.andannn.network.common.schemas.STAFF_PAGE_QUERY_SCHEMA
import me.andannn.network.common.schemas.TOGGLE_FAVORITE_MUTATION_SCHEMA
import me.andannn.network.common.schemas.USER_DATA_MUTATION_SCHEMA
import me.andannn.network.engine.mock.ACTIVITY_PAGE_DATA
import me.andannn.network.engine.mock.AIRING_SCHEDULE_PAGE_DATA
import me.andannn.network.engine.mock.CHARACTER_DETAIL_DATA
import me.andannn.network.engine.mock.CHARACTER_PAGE_DATA
import me.andannn.network.engine.mock.DETAIL_ANIME_DATA
import me.andannn.network.engine.mock.DETAIL_STAFF_DATA
import me.andannn.network.engine.mock.MEDIA_LIST_ITEM_DATA
import me.andannn.network.engine.mock.MEDIA_LIST_PAGE_DATA
import me.andannn.network.engine.mock.MEDIA_PAGE_DATA
import me.andannn.network.engine.mock.SEARCH_CHARACTER_RESULT_PAGE_DATA
import me.andannn.network.engine.mock.SEARCH_MEDIA_RESULT_PAGE_DATA
import me.andannn.network.engine.mock.SEARCH_STUDIO_RESULT_PAGE_DATA
import me.andannn.network.engine.mock.STAFF_PAGE_DATA
import me.andannn.network.engine.mock.TOGGLE_FAVORITE_RESULT
import me.andannn.network.engine.mock.UNAUTHORIZED_ERROR
import me.andannn.network.engine.mock.USER_DATA

val MockHttpClientEngine =
    MockEngine.create {
        addHandler { request ->
            val hasToken = request.headers["Authorization"] == "Bearer DummyAccessToken"
            request.body
                .takeIf { body ->
                    body is TextContent
                }?.let { body ->
                    body as TextContent
                    Json.decodeFromString<GraphQLBody>(body.text).let { query ->
                        println(query)
                        when (query.query) {
                            MEDIA_DETAIL_QUERY_SCHEMA -> {
                                respondString(DETAIL_ANIME_DATA)
                            }

                            USER_DATA_MUTATION_SCHEMA -> {
                                if (hasToken) {
                                    respondString(USER_DATA)
                                } else {
                                    respondUnAuthed()
                                }
                            }

                            MEDIA_PAGE_QUERY_SCHEMA -> {
                                respondString(MEDIA_PAGE_DATA)
                            }

                            CHARACTER_PAGE_QUERY_SCHEMA -> {
                                respondString(CHARACTER_PAGE_DATA)
                            }

                            STAFF_PAGE_QUERY_SCHEMA -> {
                                respondString(STAFF_PAGE_DATA)
                            }

                            MEDIA_LIST_QUERY_SCHEMA -> {
                                respondString(MEDIA_LIST_ITEM_DATA)
                            }

                            MEDIA_LIST_PAGE_QUERY_SCHEMA -> {
                                respondString(MEDIA_LIST_PAGE_DATA)
                            }

                            AIRING_SCHEDULE_QUERY_SCHEMA -> {
                                respondString(AIRING_SCHEDULE_PAGE_DATA)
                            }

                            SEARCH_MEDIA_QUERY_SCHEMA -> {
                                respondString(SEARCH_MEDIA_RESULT_PAGE_DATA)
                            }

                            SEARCH_CHARACTER_QUERY_SCHEMA -> {
                                respondString(SEARCH_CHARACTER_RESULT_PAGE_DATA)
                            }

                            SEARCH_STUDIO_QUERY_SCHEMA -> {
                                respondString(SEARCH_STUDIO_RESULT_PAGE_DATA)
                            }

                            SEARCH_STAFF_QUERY_SCHEMA -> {
                                respondString(SEARCH_STUDIO_RESULT_PAGE_DATA)
                            }

                            ACTIVITY_PAGE_QUERY_SCHEMA -> {
                                respondString(ACTIVITY_PAGE_DATA)
                            }

                            TOGGLE_FAVORITE_MUTATION_SCHEMA -> {
                                if (!hasToken) {
                                    respondUnAuthed()
                                } else {
                                    respondString(TOGGLE_FAVORITE_RESULT)
                                }
                            }

                            CHARACTER_DETAIL_QUERY_SCHEMA -> {
                                respondString(CHARACTER_DETAIL_DATA)
                            }

                            STAFF_DETAIL_QUERY_SCHEMA -> {
                                respondString(DETAIL_STAFF_DATA)
                            }

                            else -> error("Not supported query: $query")
                        }
                    }
                }
                ?: run {
                    error("Unsupported request body: ${request.body}")
                }
        }
    }

private fun MockRequestHandleScope.respondString(string: String): HttpResponseData =
    respond(
        content = string,
        status = HttpStatusCode.OK,
        headers = headersOf("Content-Type" to listOf("application/json")),
    )

private fun MockRequestHandleScope.respondUnAuthed(): HttpResponseData =
    respond(
        content = UNAUTHORIZED_ERROR,
        status = HttpStatusCode.Unauthorized,
        headers = headersOf("Content-Type" to listOf("application/json")),
    )
