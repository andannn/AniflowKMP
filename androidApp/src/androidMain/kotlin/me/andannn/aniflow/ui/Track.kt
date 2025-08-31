/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.TrackUiDataProvider
import me.andannn.aniflow.data.model.TrackUiState
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.util.rememberUserTitle
import me.andannn.aniflow.ui.widget.MediaRowItem
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "TrackViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class TrackViewModel(
    private val dataProvider: TrackUiDataProvider,
) : ViewModel() {
    private val _state = MutableStateFlow(TrackUiState())
    val state = _state.asStateFlow()

    init {
        Napier.d(tag = TAG) { "TrackViewModel initialized" }
        viewModelScope.launch {
            dataProvider.trackUiDataFlow().collect {
                Napier.d(tag = TAG) { "Track data updated: ${it.hashCode()}" }
                _state.value = it
            }
        }

        viewModelScope.launch {
            dataProvider.trackUiSideEffect(forceRefreshFirstTime = false).collect {
                Napier.d(tag = TAG) { "Track error: $it" }
            }
        }
    }
}

@Composable
fun Track(
    modifier: Modifier = Modifier,
    viewModel: TrackViewModel = koinViewModel(),
) {
    val content by viewModel.state.collectAsStateWithLifecycle()
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        TrackContent(
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackContent(
    content: TrackUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        content.categoryWithItems.forEach { (category, items) ->
            if (items.isNotEmpty()) {
                stickyHeader(
                    key = "header_${category.name}",
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 12.dp, start = 8.dp),
                            text = category.title,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
                itemsIndexed(
                    items = items,
                    key = { index, item -> item.mediaListModel.id },
                ) { index, item ->
                    val isFirst = index == 0
                    val isLast = index == items.lastIndex
                    Column {
                        MediaRowItem(
                            item = item,
                            shape = listItemShape(isFirst, isLast),
                            titleMaxLines = Int.MAX_VALUE,
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun listItemShape(
    isFirst: Boolean,
    isLast: Boolean,
): RoundedCornerShape {
    val edgeConerSize = MaterialTheme.shapes.large.topEnd
    val middleConerSize = MaterialTheme.shapes.extraSmall.topStart

    return if (isFirst) {
        RoundedCornerShape(
            topStart = edgeConerSize,
            topEnd = edgeConerSize,
            bottomStart = middleConerSize,
            bottomEnd = middleConerSize,
        )
    } else if (isLast) {
        RoundedCornerShape(
            topStart = middleConerSize,
            topEnd = middleConerSize,
            bottomStart = edgeConerSize,
            bottomEnd = edgeConerSize,
        )
    } else {
        RoundedCornerShape(
            topStart = middleConerSize,
            topEnd = middleConerSize,
            bottomStart = middleConerSize,
            bottomEnd = middleConerSize,
        )
    }
}
