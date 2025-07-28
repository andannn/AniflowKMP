/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.dto.PageWrapper
import me.andannn.aniflow.service.dto.UpdateUserRespond
import me.andannn.network.engine.MockHttpClientEngine
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class AniListServiceTest {
    private val testScope = TestScope()
    private val serviceWithNoToken: AniListService =
        AniListService(
            engine = MockHttpClientEngine,
            tokenProvider = NoneTokenProvider,
        )
    private val serviceWithDummyToken: AniListService =
        AniListService(
            engine = MockHttpClientEngine,
            tokenProvider = DummyTokenProvider,
        )

    @Test
    fun testGetDetailMedia() =
        testScope.runTest {
            val respond = serviceWithNoToken.getDetailMedia(id = 1)
            assertIs<MediaDetailResponse>(respond)
        }

    @Test
    fun testGetMediaPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getMediaPage(
                    page = 1,
                    perPage = 10,
                )
            assertIs<PageWrapper<Media>>(respond)
        }

    @Test
    fun testGetAuthedUserWithNoToken() =
        testScope.runTest {
            assertFailsWith<UnauthorizedException>(message = "No access token available") {
                serviceWithNoToken.getAuthedUserData()
            }
        }

    @Test
    fun testGetAuthedUserWithDummyToken() =
        testScope.runTest {
            val respond = serviceWithDummyToken.getAuthedUserData()
            assertIs<UpdateUserRespond>(respond)
        }
}

private val NoneTokenProvider =
    object : TokenProvider {
        override suspend fun getAccessToken(): String? = null
    }

private val DummyTokenProvider =
    object : TokenProvider {
        override suspend fun getAccessToken(): String? = "DummyAccessToken"
    }
