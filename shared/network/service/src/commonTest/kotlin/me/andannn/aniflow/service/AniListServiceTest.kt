/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import me.andannn.aniflow.service.dto.ActivityUnion
import me.andannn.aniflow.service.dto.AiringSchedule
import me.andannn.aniflow.service.dto.Character
import me.andannn.aniflow.service.dto.CharactersConnection
import me.andannn.aniflow.service.dto.Media
import me.andannn.aniflow.service.dto.MediaDetailResponse
import me.andannn.aniflow.service.dto.MediaList
import me.andannn.aniflow.service.dto.NotificationUnion
import me.andannn.aniflow.service.dto.Page
import me.andannn.aniflow.service.dto.Staff
import me.andannn.aniflow.service.dto.StaffConnection
import me.andannn.aniflow.service.dto.Studio
import me.andannn.aniflow.service.dto.User
import me.andannn.aniflow.service.dto.enums.MediaType
import me.andannn.aniflow.service.dto.enums.ScoreFormat
import me.andannn.aniflow.service.dto.enums.StaffLanguage
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
            assertIs<Page<Media>>(respond)
        }

    @Test
    fun testGetAuthedUserWithNoToken() =
        testScope.runTest {
            assertFailsWith<ServerException>(message = "No access token available") {
                serviceWithNoToken.getAuthedUserData()
            }
        }

    @Test
    fun testGetAuthedUserWithDummyToken() =
        testScope.runTest {
            val respond = serviceWithDummyToken.getAuthedUserData()
            assertIs<User>(respond)
        }

    @Test
    fun testGetCharacterPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getCharacterPagesOfMedia(
                    mediaId = 1,
                    page = 1,
                    perPage = 10,
                    staffLanguage = StaffLanguage.ENGLISH,
                )
            assertIs<CharactersConnection>(respond)
        }

    @Test
    fun testGetStaffPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getStaffPagesOfMedia(
                    mediaId = 1,
                    page = 1,
                    perPage = 10,
                )
            assertIs<StaffConnection>(respond)
        }

    @Test
    fun testGetMediaListItem() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getMediaListItem(
                    mediaId = 1,
                    userId = 1,
                    scoreFormat = ScoreFormat.POINT_10_DECIMAL,
                )
            assertIs<MediaList>(respond)
        }

    @Test
    fun testGetMediaListPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getMediaListPage(
                    userId = 1,
                    page = 1,
                    perPage = 10,
                    statusIn = listOf(),
                    type = MediaType.ANIME,
                    format = ScoreFormat.POINT_10_DECIMAL,
                )
            assertIs<Page<MediaList>>(respond)
        }

    @Test
    fun testGetAiringSchedulePage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getAiringSchedulePage(
                    page = 1,
                    perPage = 10,
                    airingAtGreater = 0,
                    airingAtLesser = 1000000,
                )
            assertIs<Page<AiringSchedule>>(respond)
        }

    @Test
    fun testGetMediaSearchPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.searchMedia(
                    page = 1,
                    perPage = 10,
                    keyword = "test",
                    type = MediaType.ANIME,
                    isAdult = false,
                )
            assertIs<Page<Media>>(respond)
        }

    @Test
    fun testGetCharacterSearchPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.searchCharacter(
                    page = 1,
                    perPage = 10,
                    keyword = "test",
                )
            assertIs<Page<Character>>(respond)
        }

    @Test
    fun testGetStudioSearchPage() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.searchStudio(
                    page = 1,
                    perPage = 10,
                    keyword = "test",
                )
            assertIs<Page<Studio>>(respond)
        }

    @Test
    fun testSearchStaff() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.searchStaff(
                    page = 1,
                    perPage = 10,
                    keyword = "test",
                )
            assertIs<Page<Staff>>(respond)
        }

    @Test
    fun testGetActivities() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getActivities(
                    page = 1,
                    perPage = 10,
                )
            assertIs<Page<ActivityUnion>>(respond)
        }

    @Test
    fun testToggleFavorite() =
        testScope.runTest {
            val respond =
                serviceWithDummyToken.toggleFavorite()
            println(respond)
        }

    @Test
    fun testGetCharacterDetail() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getCharacterDetail(
                    id = 1,
                )
            assertIs<Character>(respond)
        }

    @Test
    fun testGetStaffDetail() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getStaffDetail(
                    staffId = 1,
                )
            assertIs<Staff>(respond)
        }

    @Test
    fun testGetStudioDetail() =
        testScope.runTest {
            val respond =
                serviceWithNoToken.getStudioDetail(
                    studioId = 1,
                )
            assertIs<Studio>(respond)
        }

    @Test
    fun testGetNotificationPage() =
        testScope.runTest {
            val respond =
                serviceWithDummyToken.getNotificationPage(
                    page = 1,
                    perPage = 10,
                )
            assertIs<Page<NotificationUnion>>(respond)
        }

    @Test
    fun testSetMediaList() =
        testScope.runTest {
            val respond =
                serviceWithDummyToken.updateMediaList()
            assertIs<MediaList>(respond)
        }

    @Test
    fun testUpdateUserSetting() =
        testScope.runTest {
            val respond =
                serviceWithDummyToken.updateUserSetting(
                    scoreFormat = ScoreFormat.POINT_10_DECIMAL,
                )
            assertIs<User>(respond)
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
