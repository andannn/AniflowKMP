/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import me.andannn.aniflow.ui.Screen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ResultStoreTest {
    private val scope = TestScope()
    private val resultStore = ResultStore()

    @Test
    fun testCancelResultStore() =
        scope.runTest {
            val job =
                launch {
                    resultStore.awaitResultOf<Boolean>(Screen.Home)
                }

            yield()
            assertEquals(1, resultStore.continuationList.size)
            job.cancel()
            assertEquals(0, resultStore.continuationList.size)

            job.join()
        }

    @Test
    fun testCancelResultStore2() =
        scope.runTest {
            val job =
                launch {
                    resultStore.awaitResultOf(Screen.Home)
                }

            yield()
            assertEquals(1, resultStore.continuationList.size)
            resultStore.cancel(Screen.Home)
            assertEquals(0, resultStore.continuationList.size)

            job.join()
        }

    @Test
    fun testAwaitResult() =
        scope.runTest {
            var result: Boolean? = null

            val job =
                launch {
                    result = resultStore.awaitResultOf<Boolean>(Screen.Home)
                    print("")
                }
            yield()
            resultStore.emit(Screen.Home, true)
            yield()
            assertEquals(true, result)
            assertEquals(0, resultStore.continuationList.size)
            job.join()
        }

    @Test
    fun testAwaitResultError() =
        scope.runTest {
            launch {
                resultStore.awaitResultOf<Boolean>(Screen.Home)
            }
            yield()
            assertFailsWith<IllegalArgumentException> {
                resultStore.emit(Screen.Home, 1000)
            }
            assertEquals(0, resultStore.continuationList.size)
        }
}
