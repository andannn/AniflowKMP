/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.paging.MediaCategoryPageComponent
import me.andannn.aniflow.data.paging.PageComponent
import me.andannn.aniflow.ui.widget.MediaItemFilledCard
import me.andannn.aniflow.ui.widget.StaggeredGridPaging
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "MediaCategoryPaging"

class MediaCategoryPagingViewModel(
    private val category: MediaCategory,
) : ViewModel(),
    PageComponent<MediaModel> by MediaCategoryPageComponent(category) {
    init {
        Napier.d(tag = TAG) { "MediaCategoryPagingViewModel init. category: $category" }
    }

    override fun onCleared() {
        Napier.d(tag = TAG) { "MediaCategoryPagingViewModel cleared. category: $category" }
        dispose()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaCategoryPaging(
    category: MediaCategory,
    modifier: Modifier = Modifier,
    viewModel: MediaCategoryPagingViewModel =
        koinViewModel<MediaCategoryPagingViewModel>(
            parameters = { parametersOf(category) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(category.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        StaggeredGridPaging(
            modifier = Modifier.padding(it),
            columns = StaggeredGridCells.Fixed(2),
            pageComponent = viewModel,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            key = { it.id },
        ) { item ->
            MediaItemFilledCard(
                modifier = Modifier.padding(4.dp),
                title = item.title?.english ?: "EEEEEEEEEE",
                coverImage = item.coverImage,
            )
        }
    }
}
