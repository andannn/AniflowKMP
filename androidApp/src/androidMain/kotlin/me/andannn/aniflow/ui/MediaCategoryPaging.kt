/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.paging.MediaCategoryPageComponent
import me.andannn.aniflow.data.paging.PageComponent
import me.andannn.aniflow.ui.widget.MediaPreviewItem
import me.andannn.aniflow.ui.widget.VerticalGridPaging
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCategoryPaging(
    category: MediaCategory,
    modifier: Modifier = Modifier,
    viewModel: MediaCategoryPagingViewModel =
        koinViewModel<MediaCategoryPagingViewModel>(
            parameters = { parametersOf(category) },
        ),
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = category.toString()) },
            )
        },
    ) {
        VerticalGridPaging(
            modifier = Modifier.padding(it),
            columns = GridCells.Fixed(2),
            pageComponent = viewModel,
            key = { it.id },
        ) { item ->
            MediaPreviewItem(
                modifier = Modifier,
                title = item.title?.english ?: "EEEEEEEEEE",
                isFollowing = false,
                coverImage = item.coverImage,
                ooClick = { },
            )
        }
    }
}
