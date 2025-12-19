/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.label
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.StudioModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.getUserTitleString
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.CommonItemFilledCard
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.FilterDropDownMenuButton
import me.andannn.aniflow.ui.widget.GroupItems
import me.andannn.aniflow.ui.widget.ToggleFavoriteButton
import me.andannn.aniflow.ui.widget.pagingGroupedItems
import me.andannn.aniflow.ui.widget.pagingItems
import me.andannn.aniflow.usecase.data.paging.PageComponent
import me.andannn.aniflow.usecase.data.paging.StudioMediaConnectionPageComponent
import me.andannn.aniflow.usecase.data.provider.DetailStudioState
import me.andannn.aniflow.usecase.data.provider.DetailStudioUiDataProvider
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailStudio"

class DetailStudioViewModel(
    val studioId: String,
    val dataProvider: DetailStudioUiDataProvider,
    private val mediaRepository: MediaRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    val isLoading: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val mediaSort: StateFlow<MediaSort>
        field = MutableStateFlow(MediaSort.START_DATE_DESC)

    val uiState =
        dataProvider.uiDataFlow().stateIn(
            viewModelScope,
            initialValue = DetailStudioState.Empty,
            started = SharingStarted.WhileSubscribed(5000),
        )

    private var toggleFavoriteJob: Job? = null
    var pagingController by mutableStateOf<PageComponent<MediaModel>>(
        PageComponent.empty(),
    )

    init {
        viewModelScope.launch {
            dataProvider.uiSideEffect(false).collect {
                Napier.d(tag = TAG) { "DetailStaffViewModel: sync status $it" }
                isLoading.value = it.isLoading()
            }
        }

        viewModelScope.launch {
            mediaSort.collect { mediaSort ->
                Napier.d(tag = TAG) { "_mediaSort changed: $mediaSort" }
                pagingController.dispose()
                pagingController =
                    StudioMediaConnectionPageComponent(
                        studioId,
                        mediaSort,
                        errorHandler = this@DetailStudioViewModel,
                    )
            }
        }
    }

    fun onToggleFavoriteClick() {
        if (toggleFavoriteJob != null && toggleFavoriteJob?.isCompleted == false) {
            Napier.d(tag = TAG) { "onToggleFavoriteClick: last job is running, ignore this click" }
            return
        }

        toggleFavoriteJob =
            viewModelScope.launch {
                val error =
                    mediaRepository.toggleStudioItemLike(
                        uiState.value.studioModel?.id ?: error("toggle Favorite failed"),
                    )
                if (error != null) submitError(error)
            }
    }

    fun setMediaSort(sort: MediaSort) {
        mediaSort.value = sort
    }

    override fun onCleared() {
        pagingController.dispose()
    }
}

@Composable
fun DetailStudio(
    studioId: String,
    viewModel: DetailStudioViewModel = koinViewModel(parameters = { parametersOf(studioId) }),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedMediaSort by viewModel.mediaSort.collectAsStateWithLifecycle()

    DetailStudioContent(
        isLoading = isLoading,
        studio = uiState.studioModel,
        userOption = uiState.userOption,
        selectedMediaSort = selectedMediaSort,
        pagingController = viewModel.pagingController,
        onToggleFavoriteClick = {
            viewModel.onToggleFavoriteClick()
        },
        onSelectMediaSort = {
            viewModel.setMediaSort(it)
        },
        onMediaClick = { media ->
            navigator.navigateTo(Screen.DetailMedia(mediaId = media.id))
        },
        onBack = {
            navigator.popBackStack()
        },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DetailStudioContent(
    modifier: Modifier = Modifier,
    userOption: UserOptions,
    selectedMediaSort: MediaSort,
    pagingController: PageComponent<MediaModel>,
    studio: StudioModel?,
    isLoading: Boolean,
    onBack: () -> Unit = {},
    onToggleFavoriteClick: () -> Unit = {},
    onMediaClick: (MediaModel) -> Unit = {},
    onSelectMediaSort: (MediaSort) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pagingItems = pagingController.items.collectAsStateWithLifecycle()
    val pagingStatus = pagingController.status.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            TopAppBar(
                colors = TopAppBarColors,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(studio?.name ?: "")
                },
                actions = {
                    if (studio != null) {
                        ToggleFavoriteButton(
                            isFavorite = studio.isFavourite,
                            onClick = onToggleFavoriteClick,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        CustomPullToRefresh(
            modifier =
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
                    .background(color = AppBackgroundColor),
            isRefreshing = isLoading,
            enable = false,
        ) {
            LazyVerticalGrid(
                contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
                columns = GridCells.Adaptive(160.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(contentAlignment = Alignment.CenterEnd) {
                        FilterDropDownMenuButton(
                            modifier = Modifier.padding(16.dp),
                            options = MediaSort.entries.map(MediaSort::label),
                            selectedIndex = MediaSort.entries.indexOf(selectedMediaSort),
                            onSelectIndex = { index ->
                                val sort = MediaSort.entries.getOrNull(index)
                                if (sort != null) onSelectMediaSort(sort)
                            },
                        )
                    }
                }

                if (selectedMediaSort == MediaSort.START_DATE_DESC || selectedMediaSort == MediaSort.START_DATE) {
                    val groups = pagingItems.value.grouped()
                    pagingGroupedItems(
                        groups = groups,
                        status = pagingStatus.value,
                        key = { item -> item.hashCode() },
                        onLoadNextPage = { pagingController.loadNextPage() },
                        itemContent = { item ->
                            val title = item.title.getUserTitleString(titleLanguage = userOption.titleLanguage)
                            CommonItemFilledCard(
                                modifier = Modifier.padding(4.dp),
                                title = title,
                                coverImage = item.coverImage,
                                onClick = {
                                    onMediaClick(item)
                                },
                            )
                        },
                    )
                } else {
                    pagingItems(
                        items = pagingItems.value,
                        status = pagingStatus.value,
                        key = { item -> item.hashCode() },
                        onLoadNextPage = { pagingController.loadNextPage() },
                    ) { item ->
                        val title = item.title.getUserTitleString(titleLanguage = userOption.titleLanguage)
                        CommonItemFilledCard(
                            modifier = Modifier.padding(4.dp),
                            title = title,
                            coverImage = item.coverImage,
                            onClick = {
                                onMediaClick(item)
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun List<MediaModel>.grouped() =
    this
        .toSet()
        .groupBy {
            it.seasonYear
        }.map { (yearOrNull, items) ->
            GroupItems(
                title = yearOrNull?.toString() ?: "TBA",
                items = items,
            )
        }
