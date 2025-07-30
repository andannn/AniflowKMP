/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertTrue

class GraphQLQueryTest {
    @Test
    fun testToQueryBody() {
        val query =
            DetailMediaQuery(
                id = 1,
                characterPage = null,
                characterPerPage = null,
                staffPage = null,
                staffPerPage = null,
            )
        query.toQueryBody().let {
            val variables = Json.decodeFromString<Map<String, JsonPrimitive>>(it.variables)
            assertTrue {
                variables.containsKey("id") && variables["id"]?.content == "1"
            }
        }
    }

    @Test
    fun testEmptyQueryBody() {
        val query = GetUserDataQuery
        query.toQueryBody().let {
            val variables = Json.decodeFromString<Map<String, JsonPrimitive>>(it.variables)
            assertTrue {
                variables.isEmpty()
            }
        }
    }

    @Test
    fun testMediaPageQueryBody() {
        val query =
            MediaPageQuery(
                page = 1,
                perPage = 10,
                type = null,
                countryCode = null,
                seasonYear = null,
                season = null,
                status = null,
                sort = null,
                formatIn = null,
                isAdult = null,
                startDateGreater = null,
                endDateLesser = null,
            )
        query.toQueryBody().let {
            val variables = Json.decodeFromString<Map<String, JsonPrimitive>>(it.variables)
            assertTrue {
                variables["page"]?.content == "1" &&
                    variables["perPage"]?.content == "10"
            }
        }
    }
}
