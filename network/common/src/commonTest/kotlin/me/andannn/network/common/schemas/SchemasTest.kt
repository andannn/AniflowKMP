package me.andannn.network.common.schemas

import kotlin.test.Test

class SchemasTest {
    @Test
    fun testBuildSchemas() {
        val schema1 = buildMediaDetailQuerySchema()
        val schema2 = buildMediaDetailQuerySchema(withCharacterConnection = true)
        val schema3 = buildMediaDetailQuerySchema(withStaffConnection = true)
        val schema4 = buildMediaDetailQuerySchema(withStudioConnection = true)

//        println(schema1)
//        println(schema2)
//        println(schema3)
        println(schema4)
    }
}
