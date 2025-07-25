package me.andannn.aniflow.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.network.engine.MockHttpClientEngine
import kotlin.test.Test

class AniListServiceTest {
    private val testScope = TestScope()
    private val service: AniListService = AniListService(
        engine = MockHttpClientEngine
    )

    @Test
    fun testGetDetailMedia() = testScope.runTest {
        service.getDetailMedia(id = 1)
    }
}