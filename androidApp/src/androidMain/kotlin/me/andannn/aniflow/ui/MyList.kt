/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AppError
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.label
import me.andannn.aniflow.data.model.define.MediaListSort
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.define.toMediaType
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.ShapeHelper
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.GroupItems
import me.andannn.aniflow.ui.widget.MediaListRowItem
import me.andannn.aniflow.ui.widget.MediaRowItem
import me.andannn.aniflow.ui.widget.pagingGroupedItems
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel

enum class MediaListStatusCategory {
    WATCHING,
    COMPLETED,
    DROPPED,
}

class MyListViewMoel(
    private val mediaRepository: MediaRepository,
    private val authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    private val _selectedMediaListStatusCategory =
        MutableStateFlow(MediaListStatusCategory.WATCHING)
    val selectedMediaListStatusCategory = _selectedMediaListStatusCategory.asStateFlow()

    val categoryWithMediaListItems =
        mutableStateMapOf<MediaListStatusCategory, ContentState>()

    init {
        viewModelScope.launch {
            val authedUser =
                authRepository.getAuthedUserFlow().first() ?: error("User not logged in")
            val mediaType = mediaRepository.getContentModeFlow().first().toMediaType()
            val userOptions = authRepository.getUserOptionsFlow().first()
            _selectedMediaListStatusCategory
                .collectLatest { category ->
                    val statusOrNull = categoryWithMediaListItems[category]
                    if (statusOrNull != null && statusOrNull is ContentState.Success) {
                        return@collectLatest
                    }

                    categoryWithMediaListItems[category] = ContentState.Loading

                    val (items, error) =
                        mediaRepository.getAllMediaListItems(
                            userId = authedUser.id,
                            mediaType = mediaType,
                            scoreFormat = userOptions.scoreFormat,
                            status = category.toStatus(),
                            sort = MediaListSort.START_DATE_DESC,
                        )

                    if (error != null) {
                        submitError(error)
                        categoryWithMediaListItems[category] = ContentState.Error(error)
                    } else {
                        categoryWithMediaListItems[category] = ContentState.Success(items)
                    }
                }
        }
    }

    fun setSelectedMediaListStatusCategory(category: MediaListStatusCategory) {
        _selectedMediaListStatusCategory.value = category
    }

    sealed class ContentState {
        object Loading : ContentState()

        data class Success(
            val items: List<MediaWithMediaListItem>,
        ) : ContentState()

        data class Error(
            val error: AppError,
        ) : ContentState()
    }
}

@Composable
fun MyList(
    modifier: Modifier = Modifier,
    viewModel: MyListViewMoel = koinViewModel(),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val selectedMediaListStatusCategory by viewModel.selectedMediaListStatusCategory.collectAsStateWithLifecycle()
    val categoryWithMediaListItems = viewModel.categoryWithMediaListItems
    val itemContentState by
        remember {
            derivedStateOf {
                categoryWithMediaListItems[selectedMediaListStatusCategory]
                    ?: MyListViewMoel.ContentState.Loading
            }
        }
    MyListContent(
        modifier = modifier,
        selectedMediaListStatusCategory = selectedMediaListStatusCategory,
        itemContentState = itemContentState,
        onCategorySelected = { category ->
            viewModel.setSelectedMediaListStatusCategory(category)
        },
        onMediaListItemClick = { mediaWithMediaListItem ->
            navigator.navigateTo(
                Screen.DetailMedia(
                    mediaId = mediaWithMediaListItem.mediaModel.id,
                ),
            )
        },
        onBack = {
            navigator.popBackStack()
        },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MyListContent(
    modifier: Modifier = Modifier,
    selectedMediaListStatusCategory: MediaListStatusCategory,
    itemContentState: MyListViewMoel.ContentState,
    onCategorySelected: (MediaListStatusCategory) -> Unit = {},
    onMediaListItemClick: (MediaWithMediaListItem) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            TopAppBar(
                colors = TopAppBarColors,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = "My List")
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
        LazyColumn(
            modifier =
                Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(AppBackgroundColor),
            contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
        ) {
            stickyHeader(key = "tabRow") {
                val options = MediaListStatusCategory.entries
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    options.forEachIndexed { index, option ->
                        ToggleButton(
                            checked = option == selectedMediaListStatusCategory,
                            onCheckedChange = {
                                onCategorySelected(option)
                            },
                            colors =
                                ToggleButtonDefaults.toggleButtonColors().copy(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                            modifier = Modifier.semantics { role = Role.RadioButton },
                        ) {
                            Text(option.label())
                        }
                    }
                }
            }

            when (itemContentState) {
                is MyListViewMoel.ContentState.Error -> {
                    // Noop
                }
                MyListViewMoel.ContentState.Loading -> {
                    item(key = "loading") {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            ContainedLoadingIndicator()
                        }
                    }
                }
                is MyListViewMoel.ContentState.Success ->
                    pagingGroupedItems(
                        groups = itemContentState.items.grouped(),
                        key = { _, item -> item.hashCode() },
                        itemContent = { isFirst, isLast, item ->
                            Column {
                                MediaListRowItem(
                                    item = item,
                                    shape = ShapeHelper.listItemShapeVertical(isFirst, isLast),
                                    titleMaxLines = Int.MAX_VALUE,
                                    userTitleLanguage = UserTitleLanguage.ENGLISH,
                                    onClick = {
                                        onMediaListItemClick(item)
                                    },
                                )
                                Spacer(Modifier.height(2.dp))
                            }
                        },
                    )
            }
        }
    }
}

private fun MediaListStatusCategory.toStatus() =
    when (this) {
        MediaListStatusCategory.WATCHING ->
            listOf(
                MediaListStatus.PLANNING,
                MediaListStatus.CURRENT,
            )

        MediaListStatusCategory.COMPLETED ->
            listOf(
                MediaListStatus.COMPLETED,
                MediaListStatus.REPEATING,
            )

        MediaListStatusCategory.DROPPED ->
            listOf(
                MediaListStatus.DROPPED,
                MediaListStatus.PAUSED,
            )
    }

private fun MediaListStatusCategory.label() =
    when (this) {
        MediaListStatusCategory.WATCHING -> "Watching"
        MediaListStatusCategory.COMPLETED -> "Completed"
        MediaListStatusCategory.DROPPED -> "Dropped"
    }

private data class AnimeSeason(
    val year: Int?,
    val season: MediaSeason?,
)

private fun List<MediaWithMediaListItem>.grouped() =
    this
        .groupBy {
            AnimeSeason(
                year = it.mediaModel.seasonYear,
                season = it.mediaModel.season,
            )
        }.map { (season, items) ->
            if (season.year == null || season.season == null) {
                GroupItems(
                    title = "TBA",
                    items = items,
                )
            } else {
                GroupItems(
                    title = "${season.year} ${season.season.label()}",
                    items = items,
                )
            }
        }
