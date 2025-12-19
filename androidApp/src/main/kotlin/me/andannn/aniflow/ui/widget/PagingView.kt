/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.usecase.data.paging.LoadingStatus
import me.andannn.aniflow.usecase.data.paging.PageComponent

private const val TAG = "PagingView"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Any> VerticalGridPaging(
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
    onLoadNextPage: () -> Unit,
    itemContent: @Composable (T) -> Unit,
) {
    items(
        items = items,
        key = key,
    ) { item ->
        itemContent(item)
    }
    item(span = { GridItemSpan(maxLineSpan) }) {
        BottomAnchor(status = status, onLoadNextPage = onLoadNextPage)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Any> VerticalListPaging(
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

        item {
            BottomAnchor(status = status, onLoadNextPage = { pageComponent.loadNextPage() })
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Any> StaggeredGridPaging(
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
    onLoadNextPage: () -> Unit,
    itemContent: @Composable (T) -> Unit,
) {
    items(
        items = items,
        key = key,
    ) { item ->
        itemContent(item)
    }

    item(
        span = StaggeredGridItemSpan.FullLine,
    ) {
        BottomAnchor(status = status, onLoadNextPage = onLoadNextPage)
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

    item(
        span = StaggeredGridItemSpan.FullLine,
    ) {
        BottomAnchor(status = status, onLoadNextPage = onLoadNextPage)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BottomAnchor(
    status: LoadingStatus,
    modifier: Modifier = Modifier,
    onLoadNextPage: () -> Unit,
) {
    if (status is LoadingStatus.Idle) {
        Box(
            modifier =
                modifier
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

    if (status is LoadingStatus.Loading) {
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

data class GroupItems<T>(
    val title: String,
    val items: List<T>,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun <T> LazyGridScope.pagingGroupedItems(
    groups: List<GroupItems<T>>,
    status: LoadingStatus,
    key: (T) -> Any,
    onLoadNextPage: () -> Unit,
    titleContent: @Composable (String) -> Unit = {
        LabelTitleContent(title = it)
    },
    itemContent: @Composable (T) -> Unit,
) {
    groups.forEach { (title, items) ->
        stickyHeader(key = title) {
            titleContent(title)
        }

        items(
            items = items,
            key = key,
        ) { item ->
            itemContent(item)
        }
    }

    item(span = { GridItemSpan(maxLineSpan) }) {
        BottomAnchor(status = status, onLoadNextPage = onLoadNextPage)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun <T> LazyListScope.pagingGroupedItems(
    groups: List<GroupItems<T>>,
    key: (Int, T) -> Any,
    itemContent: @Composable (Boolean, Boolean, T) -> Unit,
    onLoadNextPage: () -> Unit = {},
    status: LoadingStatus = LoadingStatus.Idle,
    titleContent: @Composable (String) -> Unit = {
        LabelTitleContent(title = it)
    },
) {
    groups.forEach { (title, items) ->
        stickyHeader(key = title) {
            titleContent(title)
        }

        itemsIndexed(
            items = items,
            key = key,
        ) { index, item ->
            val isFirst = index == 0
            val isLast = index == items.lastIndex
            itemContent(isFirst, isLast, item)
        }
    }

    item(key = "bottomAnchor") {
        BottomAnchor(status = status, onLoadNextPage = onLoadNextPage)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LabelTitleContent(title: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            color = AppBackgroundColor,
            shape = RoundedCornerShape(bottomEnd = 12.dp),
        ) {
            Text(
                modifier = Modifier.padding(end = 12.dp, top = 24.dp),
                text = title,
                style = MaterialTheme.typography.headlineMediumEmphasized,
            )
        }
    }
}
