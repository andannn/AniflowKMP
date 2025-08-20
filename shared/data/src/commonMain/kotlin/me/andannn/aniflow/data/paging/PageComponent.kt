/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.paging

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.model.Page
import me.andannn.aniflow.data.model.PageInfo

sealed interface LoadingStatus {
    data object Idle : LoadingStatus

    data object AllLoaded : LoadingStatus

    data object Loading : LoadingStatus

    data class Error(
        val error: Throwable,
    ) : LoadingStatus
}

interface PageComponent<T> {
    @NativeCoroutines
    val items: StateFlow<List<T>>

    @NativeCoroutines
    val status: StateFlow<LoadingStatus>

    fun loadNextPage()

    /**
     * Disposes the component and cancels any ongoing operations.
     * This should be called when the component is no longer needed
     */
    fun dispose()
}

val DEFAULT_CONFIG =
    PageConfig(
        perPage = 20,
    )

data class PageConfig(
    val perPage: Int,
)

private const val TAG = "PageComponent"

internal class DefaultPageComponent<T>(
    private val config: PageConfig,
    private val onLoadPage: suspend (page: Int, perPage: Int) -> Page<T>,
) : PageComponent<T>,
    CoroutineScope {
    override val coroutineContext = Dispatchers.Main + Job()

    override val items = MutableStateFlow<List<T>>(emptyList())

    override val status = MutableStateFlow<LoadingStatus>(LoadingStatus.Idle)

    private val currentPageInfo = MutableStateFlow<PageInfo?>(null)

    private val currentPageIndex: Int
        get() = currentPageInfo.value?.currentPage ?: 0

    init {
        launch {
            Napier.d(tag = TAG) { "loadPage load initial page." }
            status.value = LoadingStatus.Loading
            loadPage(1)
        }
    }

    override fun loadNextPage() {
        Napier.d(tag = TAG) { "loadNextPage E" }
        if (status.value is LoadingStatus.Loading || status.value is LoadingStatus.AllLoaded) {
            Napier.d(tag = TAG) { "loadNextPage return because of state ${status.value}" }
            return
        }
        status.value = LoadingStatus.Loading

        val currentPage = currentPageInfo.value

        if (currentPage == null || !currentPage.hasNextPage) {
            Napier.d(tag = TAG) { "loadNextPage return because of currentPage $currentPage" }
            // If there is no current page or no next page, we cannot load more.
            return
        }

        launch {
            Napier.d(tag = TAG) { "loadNextPage." }
            status.value = LoadingStatus.Loading
            loadPage(currentPageIndex + 1)
        }
    }

    private suspend fun loadPage(page: Int) {
        Napier.d(tag = TAG) { "loadPage start $page" }
        try {
            val page = onLoadPage(page, config.perPage)
            Napier.d(tag = TAG) { "loadPage api returned ${page.pageInfo}" }
            items.value = items.value + page.items
            currentPageInfo.value = page.pageInfo
            if (page.pageInfo.hasNextPage) {
                status.value = LoadingStatus.Idle
            } else {
                status.value = LoadingStatus.AllLoaded
            }
        } catch (e: Exception) {
            status.value = LoadingStatus.Error(e)
        }
        Napier.d(tag = TAG) { "loadPage State to ${status.value}" }
    }

    override fun dispose() {
        cancel()
    }
}
