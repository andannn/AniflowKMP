/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
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
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.getUserTitleString
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.StyledReadingContentFontFamily
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.util.appendItem
import me.andannn.aniflow.ui.util.format
import me.andannn.aniflow.ui.widget.CommonItemFilledCard
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.FilterDropDownMenuButton
import me.andannn.aniflow.ui.widget.ToggleFavoriteButton
import me.andannn.aniflow.ui.widget.pagingItems
import me.andannn.aniflow.usecase.data.paging.CharacterDetailMediaPaging
import me.andannn.aniflow.usecase.data.paging.PageComponent
import me.andannn.aniflow.usecase.data.provider.DetailCharacterUiDataProvider
import me.andannn.aniflow.usecase.data.provider.DetailCharacterUiState
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailCharacter"

class DetailCharacterViewModel(
    private val characterId: String,
    private val dataProvider: DetailCharacterUiDataProvider,
    private val mediaRepository: MediaRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    val isLoading: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val mediaSort: StateFlow<MediaSort>
        field = MutableStateFlow(MediaSort.START_DATE_DESC)

    var pagingController by mutableStateOf<PageComponent<MediaModel>>(
        PageComponent.empty(),
    )

    private var toggleFavoriteJob: Job? = null

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
                    CharacterDetailMediaPaging(
                        characterId,
                        mediaSort,
                        errorHandler = this@DetailCharacterViewModel,
                    )
            }
        }
    }

    val uiState =
        dataProvider.uiDataFlow().stateIn(
            viewModelScope,
            initialValue = DetailCharacterUiState.Empty,
            started = SharingStarted.WhileSubscribed(5000),
        )

    fun setMediaSort(sort: MediaSort) {
        mediaSort.value = sort
    }

    fun onToggleFavoriteClick() {
        if (toggleFavoriteJob != null && toggleFavoriteJob?.isCompleted == false) {
            Napier.d(tag = TAG) { "onToggleFavoriteClick: last job is running, ignore this click" }
            return
        }

        toggleFavoriteJob =
            viewModelScope.launch {
                val error =
                    mediaRepository.toggleCharacterItemLike(
                        uiState.value.characterModel?.id ?: error("toggle Favorite failed"),
                    )
                if (error != null) submitError(error)
            }
    }
}

@Composable
fun DetailCharacter(
    characterId: String,
    viewModel: DetailCharacterViewModel =
        koinViewModel(
            parameters = { parametersOf(characterId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedMediaSort by viewModel.mediaSort.collectAsStateWithLifecycle()
    DetailCharacterContent(
        title = uiState.title,
        isLoading = isLoading,
        character = uiState.characterModel,
        selectedMediaSort = selectedMediaSort,
        options = uiState.userOption,
        pagingController = viewModel.pagingController,
        onBack = { navigator.popBackStack() },
        onSelectMediaSort = viewModel::setMediaSort,
        onToggleFavoriteClick = viewModel::onToggleFavoriteClick,
        onMediaClick = { navigator.navigateTo(Screen.DetailMedia(it.id)) },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun DetailCharacterContent(
    title: String,
    modifier: Modifier = Modifier,
    selectedMediaSort: MediaSort,
    isLoading: Boolean,
    character: CharacterModel?,
    options: UserOptions,
    pagingController: PageComponent<MediaModel>,
    onBack: () -> Unit,
    onSelectMediaSort: (MediaSort) -> Unit = {},
    onMediaClick: (MediaModel) -> Unit = {},
    onToggleFavoriteClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            MediumFlexibleTopAppBar(
                colors = TopAppBarColors,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(title)
                },
                subtitle = {
                    character?.name?.alternative?.let { names ->
                        Text(
                            text = names.joinToString(", "),
                        )
                    }
                },
                actions = {
                    if (character != null) {
                        ToggleFavoriteButton(
                            isFavorite = character.isFavourite == true,
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
            val pagingItems = pagingController.items.collectAsStateWithLifecycle()
            val pagingStatus = pagingController.status.collectAsStateWithLifecycle()
            LazyVerticalStaggeredGrid(
                contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
                columns = StaggeredGridCells.Adaptive(160.dp),
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Spacer(Modifier.weight(1f))
                        Surface(
                            modifier =
                                Modifier
                                    .weight(2f)
                                    .fillMaxWidth(),
                            shape = MaterialTheme.shapes.largeIncreased,
                        ) {
                            AsyncImage(
                                model = character?.image,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    val description =
                        remember(character) {
                            character?.description?.let {
                                AnnotatedString.fromHtml(it)
                            }
                        }
                    val text =
                        buildAnnotatedString {
                            Log.d(TAG, "DetailCharacterContent: ${character?.age}")
                            Log.d(TAG, "DetailCharacterContent: ${character?.dateOfBirth}")
                            character?.dateOfBirth?.let {
                                appendItem(
                                    "Birthday",
                                    it.format(),
                                )
                            }
                            character?.age?.let {
                                appendItem("Age", it)
                            }
                            character?.gender?.let {
                                appendItem("Gender", it)
                            }
                            character?.bloodType?.let {
                                appendItem(
                                    "BloodType",
                                    it,
                                )
                            }
                            description?.let {
                                append(it)
                            }
                        }

                    Text(
                        text = text,
                        fontFamily = StyledReadingContentFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                    )
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(contentAlignment = Alignment.CenterEnd) {
                        FilterDropDownMenuButton(
                            modifier =
                                Modifier.padding(16.dp),
                            options = MediaSort.entries.map { it.label() },
                            selectedIndex = MediaSort.entries.indexOf(selectedMediaSort),
                            onSelectIndex = { index ->
                                onSelectMediaSort(MediaSort.entries[index])
                            },
                        )
                    }
                }

                pagingItems(
                    items = pagingItems.value,
                    status = pagingStatus.value,
                    key = { item -> item.hashCode() },
                    onLoadNextPage = { pagingController.loadNextPage() },
                ) { item ->
                    val title = item.title.getUserTitleString(titleLanguage = options.titleLanguage)
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
