package me.andannn.aniflow.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.network.engine.MockHttpClientEngine
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class AniListServiceTest {
    private val testScope = TestScope()
    private val service: AniListService =
        AniListService(
            engine = MockHttpClientEngine,
        )

    @Test
    fun testGetDetailMedia() =
        testScope.runTest {
            val respond = service.getDetailMedia(id = 1)
            assertIs<MediaDetailResponse>(respond)
        }

    @Test
    fun testGetAuthedUser() =
        testScope.runTest {
            assertFailsWith<UnauthorizedException> {
                service.getAuthedUserData()
            }
        }
}
