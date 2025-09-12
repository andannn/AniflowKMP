/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.paging

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AppErrorHandler
import me.andannn.aniflow.data.LoadingStatus
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.model.Page
import me.andannn.aniflow.data.model.PageInfo

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
    private val onLoadPage: suspend (page: Int, perPage: Int) -> Pair<Page<T>, AppError?>,
    private val errorHandler: AppErrorHandler? = null,
) : PageComponent<T>,
    CoroutineScope {
    init {
        Napier.d(tag = TAG) { "DefaultPageComponent init. ${this.hashCode()}" }
    }

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
        val (page, error) = onLoadPage(page, config.perPage)
        if (error != null) {
            status.value = LoadingStatus.Error(error)

            errorHandler?.submitError(error)
            return
        }

        Napier.d(tag = TAG) { "loadPage api returned ${page.pageInfo}" }
        items.value = items.value + page.items
        currentPageInfo.value = page.pageInfo
        if (page.pageInfo.hasNextPage) {
            status.value = LoadingStatus.Idle
        } else {
            status.value = LoadingStatus.AllLoaded
        }

        Napier.d(tag = TAG) { "loadPage State to ${status.value}" }
    }

    override fun dispose() {
        Napier.d(tag = TAG) { "dispose ${this.hashCode()}" }
        cancel()
    }
}
