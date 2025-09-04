/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DiscoverUiDataProvider
import me.andannn.aniflow.data.HomeAppBarUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.Screen
import me.andannn.aniflow.data.model.DiscoverUiState
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.relation.CategoryWithContents
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.data.util.getUserTitleString
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.MediaPreviewItem
import me.andannn.aniflow.ui.widget.NewReleaseCard
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "Discover"

class DiscoverViewModel(
    private val discoverDataProvider: DiscoverUiDataProvider,
    private val appbarDataProvider: HomeAppBarUiDataProvider,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState.Empty)
    val state = _state.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val appBarState =
        appbarDataProvider.appBarFlow().stateIn(
            viewModelScope,
            initialValue = HomeAppBarUiState(),
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5000),
        )
    private var sideEffectJob: Job? = null

    init {
        cancelLastAndRegisterUiSideEffect()
        viewModelScope.launch {
            discoverDataProvider.discoverUiDataFlow().collect {
                _state.value = it
            }
        }
    }

    fun onMediaClick(media: MediaModel) {
        Napier.d(tag = TAG) { "Media clicked:" }
    }

    fun onPullRefresh() {
        Napier.d(tag = TAG) { "onPullRefresh:" }
        cancelLastAndRegisterUiSideEffect(force = true)
    }

    fun changeContentMode(mode: MediaContentMode) {
        Napier.d(tag = TAG) { "changeContentMode: $mode" }
        viewModelScope.launch {
            mediaRepository.setContentMode(mode)
        }
    }

    private fun cancelLastAndRegisterUiSideEffect(force: Boolean = false) {
        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect:" }
        sideEffectJob?.cancel()
        sideEffectJob =
            viewModelScope.launch {
                discoverDataProvider.discoverUiSideEffect(force).collect { status ->
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
    val appBarState by discoverViewModel.appBarState.collectAsStateWithLifecycle()
    DiscoverContent(
        isRefreshing = isRefreshing,
        appbarState = appBarState,
        categoryDataList = state.categoryDataMap.content,
        newReleasedMedia = state.newReleasedMedia,
        userTitleLanguage = state.userOptions.titleLanguage,
        onContentTypeChange = discoverViewModel::changeContentMode,
        onAuthIconClick = {
            navigator.navigateTo(Screen.Dialog.Login)
        },
        onMediaClick = {
            navigator.navigateTo(Screen.Notification)
        },
        onPullRefresh = discoverViewModel::onPullRefresh,
        onNavigateToMediaCategory = { category ->
            navigator.navigateTo(Screen.MediaCategoryList(category))
        },
        modifier = modifier,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
fun DiscoverContent(
    isRefreshing: Boolean,
    appbarState: HomeAppBarUiState,
    modifier: Modifier = Modifier,
    userTitleLanguage: UserTitleLanguage,
    categoryDataList: List<CategoryWithContents>,
    newReleasedMedia: List<MediaWithMediaListItem>,
    onMediaClick: (MediaModel) -> Unit,
    onPullRefresh: () -> Unit,
    onNavigateToMediaCategory: (MediaCategory) -> Unit = {},
    onContentTypeChange: (MediaContentMode) -> Unit = {},
    onAuthIconClick: () -> Unit = {},
) {
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier =
            modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        topBar = {
            DefaultAppBar(
                title = "AniFlow",
                state = appbarState,
                scrollBehavior = appBarScrollBehavior,
                onContentTypeChange = onContentTypeChange,
                onAuthIconClick = onAuthIconClick,
            )
        },
    ) {
        CustomPullToRefresh(
            modifier =
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
            isRefreshing = isRefreshing,
            onPullRefresh = onPullRefresh,
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                item(
                    key = "New release",
                ) {
                    val visible = rememberUpdatedState(newReleasedMedia.isNotEmpty())
                    Box(
                        modifier =
                            Modifier.animateContentSize(
                                animationSpec =
                                    spring(
                                        stiffness = Spring.StiffnessMedium,
                                        visibilityThreshold = IntSize.VisibilityThreshold,
                                    ),
                            ),
                    ) {
                        if (visible.value) {
                            NewReleaseCard(
                                items = newReleasedMedia,
                                userTitleLanguage = userTitleLanguage,
                            )
                        } else {
                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                            )
                        }
                    }
                }

                items(
                    items = categoryDataList,
                    key = { it.category },
                ) { (category, items) ->
                    TitleWithContent(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        title = category.title,
                        onMoreClick = {
                            onNavigateToMediaCategory(category)
                        },
                    ) {
                        MediaPreviewSector(
                            mediaList = items,
                            userTitleLanguage = userTitleLanguage,
                            onMediaClick = onMediaClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaPreviewSector(
    mediaList: List<MediaModel>,
    userTitleLanguage: UserTitleLanguage,
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
                val title by rememberUpdatedState(it.title.getUserTitleString(userTitleLanguage))
                Row {
                    MediaPreviewItem(
                        modifier = Modifier.width(150.dp),
                        title = title,
                        isFollowing = false,
                        coverImage = it.coverImage,
                        ooClick = { onMediaClick(it) },
                    )
                    Spacer(Modifier.width(4.dp))
                }
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
                    .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, maxLines = 1, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            IconButton(onMoreClick, shapes = IconButtonDefaults.shapes()) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
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
