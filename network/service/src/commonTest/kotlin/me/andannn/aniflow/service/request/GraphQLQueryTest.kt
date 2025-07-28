package me.andannn.aniflow.service.request

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertTrue

class GraphQLQueryTest {
    @Test
    fun testToQueryBody() {
        val query = DetailMediaQuery(id = 1)
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
