/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DiscoverUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.DiscoverUiState
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.ui.widget.MediaPreviewItem
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "Discover"

class DiscoverViewModel(
    private val dataProvider: DiscoverUiDataProvider,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState.Empty)
    val state = _state.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private var sideEffectJob: Job? = null

    init {
        cancelLastAndRegisterUiSideEffect()
        viewModelScope.launch {
            dataProvider.discoverUiDataFlow().collect {
                _state.value = it
            }
        }
    }

    fun onMediaClick(media: MediaModel) {
        Napier.d(tag = TAG) { "Media clicked:" }
        viewModelScope.launch {
            authRepository.startLoginProcessAndWaitResult()
        }
    }

    fun onPullRefresh() {
        Napier.d(tag = TAG) { "onPullRefresh:" }
        cancelLastAndRegisterUiSideEffect(force = true)
    }

    private fun cancelLastAndRegisterUiSideEffect(force: Boolean = false) {
        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect:" }
        sideEffectJob?.cancel()
        sideEffectJob =
            viewModelScope.launch {
                dataProvider.discoverUiSideEffect(force).collect { status ->
                    Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect: sync status $status" }
                    _isRefreshing.value = status.isLoading()
                }
            }
    }
}

@Composable
fun Discover(
    modifier: Modifier = Modifier,
    discoverViewModel: DiscoverViewModel = koinViewModel(),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val state by discoverViewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by discoverViewModel.isRefreshing.collectAsStateWithLifecycle()
    DiscoverContent(
        isRefreshing = isRefreshing,
        categoryDataList = state.categoryDataMap.content,
        onMediaClick = discoverViewModel::onMediaClick,
        onPullRefresh = discoverViewModel::onPullRefresh,
        onNavigateToMediaCategory = { category ->
            navigator.navigateTo(Screen.MediaCategoryList(category))
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DiscoverContent(
    isRefreshing: Boolean,
    categoryDataList: List<CategoryWithContents>,
    modifier: Modifier = Modifier,
    onMediaClick: (MediaModel) -> Unit,
    onPullRefresh: () -> Unit,
    onNavigateToMediaCategory: (MediaCategory) -> Unit = {},
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onPullRefresh,
        state = state,
        indicator = {
            PullToRefreshDefaults.IndicatorBox(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                elevation = 0.dp,
            ) {
                if (isRefreshing) {
                    ContainedLoadingIndicator()
                } else {
                    ContainedLoadingIndicator(
                        progress = { state.distanceFraction },
                    )
                }
            }
        },
    ) {
        LazyColumn {
            items(
                items = categoryDataList,
                key = { it.category },
            ) { (category, items) ->
                TitleWithContent(
                    modifier = Modifier.fillMaxWidth(),
                    title = category.title,
                    onMoreClick = {
                        onNavigateToMediaCategory(category)
                    },
                ) {
                    MediaPreviewSector(
                        mediaList = items,
                        onMediaClick = onMediaClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaPreviewSector(
    mediaList: List<MediaModel>,
    modifier: Modifier = Modifier,
    onMediaClick: (MediaModel) -> Unit = {},
) {
    val isLoading by rememberUpdatedState(mediaList.isEmpty())

    LazyRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (isLoading) {
            repeat(6) {
                item {
                    Surface(
                        modifier =
                            Modifier
                                .width(240.dp)
                                .aspectRatio(3 / 4f),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {}
                }
            }
        } else {
            items(
                mediaList,
                key = { it.id },
            ) {
                MediaPreviewItem(
                    modifier = Modifier.width(240.dp),
                    title = it.title?.english ?: "EEEEEEEEEE",
                    isFollowing = false,
                    coverImage = it.coverImage,
                    ooClick = { onMediaClick(it) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TitleWithContent(
    title: String,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Column(modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, maxLines = 1, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            TextButton(onMoreClick, shapes = ButtonDefaults.shapes()) {
                Text("More")
            }
        }
        content()
    }
}

val MediaCategory.title
    get() =
        when (this) {
            MediaCategory.CURRENT_SEASON_ANIME -> "Popular this season"
            MediaCategory.NEXT_SEASON_ANIME -> "Upcoming next season"
            MediaCategory.TRENDING_ANIME -> "Trending now"
            MediaCategory.MOVIE_ANIME -> "Movie"
            MediaCategory.TRENDING_MANGA -> "Trending manga"
            MediaCategory.ALL_TIME_POPULAR_MANGA -> "All time popular manga"
            MediaCategory.TOP_MANHWA -> "Top manhwa"
            MediaCategory.NEW_ADDED_ANIME -> "New Added Anime"
            MediaCategory.NEW_ADDED_MANGA -> "New added manga"
        }
