/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.LoadingStatus
import me.andannn.aniflow.data.PageComponent

private const val TAG = "PagingView"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> VerticalGridPaging(
    modifier: Modifier = Modifier,
    columns: GridCells,
    pageComponent: PageComponent<T>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    val items by pageComponent.items.collectAsStateWithLifecycle()
    val status by pageComponent.status.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        modifier = modifier,
        columns = columns,
        contentPadding = contentPadding,
    ) {
        pagingItems(
            items = items,
            status = status,
            key = key,
            itemContent = itemContent,
            onLoadNextPage = { pageComponent.loadNextPage() },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun <T> LazyGridScope.pagingItems(
    items: List<T>,
    status: LoadingStatus,
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
    onLoadNextPage: () -> Unit,
) {
    items(
        items = items,
        key = key,
    ) { item ->
        itemContent(item)
    }

    if (status is LoadingStatus.Idle) {
        item {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .onVisibilityChanged(
                            minFractionVisible = 0.3f,
                            callback = { visible ->
                                Napier.d(tag = TAG) { "Bottom widget visibility changed: $visible" }
                                if (visible) {
                                    onLoadNextPage()
                                }
                            },
                        ),
            )
        }
    }

    if (status is LoadingStatus.Loading) {
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                ContainedLoadingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> VerticalListPaging(
    modifier: Modifier = Modifier,
    pageComponent: PageComponent<T>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: (index: Int, T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    val items by pageComponent.items.collectAsStateWithLifecycle()
    val status by pageComponent.status.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(
            items = items,
            key = key,
        ) { index, item ->
            itemContent(item)
        }

        if (status is LoadingStatus.Idle) {
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .onVisibilityChanged(
                                minFractionVisible = 0.3f,
                                callback = { visible ->
                                    Napier.d(tag = TAG) { "Bottom widget visibility changed: $visible" }
                                    if (visible) {
                                        pageComponent.loadNextPage()
                                    }
                                },
                            ),
                )
            }
        }

        if (status is LoadingStatus.Loading) {
            item {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    ContainedLoadingIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> StaggeredGridPaging(
    modifier: Modifier = Modifier,
    columns: StaggeredGridCells,
    pageComponent: PageComponent<T>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    val items by pageComponent.items.collectAsStateWithLifecycle()
    val status by pageComponent.status.collectAsStateWithLifecycle()

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = columns,
        contentPadding = contentPadding,
    ) {
        pagingItems(
            items = items,
            status = status,
            key = key,
            itemContent = itemContent,
            onLoadNextPage = { pageComponent.loadNextPage() },
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun <T> LazyStaggeredGridScope.pagingItems(
    items: List<T>,
    status: LoadingStatus,
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
    onLoadNextPage: () -> Unit,
) {
    items(
        items = items,
        key = key,
    ) { item ->
        itemContent(item)
    }

    if (status is LoadingStatus.Idle) {
        item {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .onVisibilityChanged(
                            minFractionVisible = 0.3f,
                            callback = { visible ->
                                Napier.d(tag = TAG) { "Bottom widget visibility changed: $visible" }
                                if (visible) {
                                    onLoadNextPage()
                                }
                            },
                        ),
            )
        }
    }

    if (status is LoadingStatus.Loading) {
        item(
            span = StaggeredGridItemSpan.FullLine,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                ContainedLoadingIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun <T> LazyStaggeredGridScope.fullLinePagingItems(
    items: List<T>,
    status: LoadingStatus,
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
    onLoadNextPage: () -> Unit,
) {
    items(
        items = items,
        key = key,
        span = { StaggeredGridItemSpan.FullLine },
    ) { item ->
        itemContent(item)
    }

    if (status is LoadingStatus.Idle) {
        item {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .onVisibilityChanged(
                            minFractionVisible = 0.3f,
                            callback = { visible ->
                                Napier.d(tag = TAG) { "Bottom widget visibility changed: $visible" }
                                if (visible) {
                                    onLoadNextPage()
                                }
                            },
                        ),
            )
        }
    }

    if (status is LoadingStatus.Loading) {
        item(
            span = StaggeredGridItemSpan.FullLine,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                contentAlignment = Alignment.Center,
            ) {
                ContainedLoadingIndicator()
            }
        }
    }
}
