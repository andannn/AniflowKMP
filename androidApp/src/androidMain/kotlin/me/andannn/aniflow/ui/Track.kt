/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.HomeAppBarUiDataProvider
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.TrackUiDataProvider
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.TrackUiState
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.submitErrorOfSyncStatus
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.ShapeHelper
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.DefaultAppBar
import me.andannn.aniflow.ui.widget.MediaRowItem
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.LocalResultStore
import me.andannn.aniflow.util.ResultStore
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "TrackViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class TrackViewModel(
    private val trackDataProvider: TrackUiDataProvider,
    private val appBarUiDataProvider: HomeAppBarUiDataProvider,
    private val mediaRepository: MediaRepository,
    private val authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    private val _state = MutableStateFlow(TrackUiState())
    val state = _state.asStateFlow()
    private var sideEffectJob: Job? = null

    private val isSideEffectRefreshing = MutableStateFlow(false)
    private var isLoginProcessing = MutableStateFlow(false)
    val isLoading =
        combine(
            isSideEffectRefreshing,
            isLoginProcessing,
        ) { isSideEffectRefreshing, isLoginProcessing ->
            isSideEffectRefreshing || isLoginProcessing
        }.stateIn(
            viewModelScope,
            initialValue = false,
            started = SharingStarted.WhileSubscribed(5000),
        )

    val appBarState =
        appBarUiDataProvider.appBarFlow().stateIn(
            viewModelScope,
            initialValue = HomeAppBarUiState(),
            started = SharingStarted.WhileSubscribed(5000),
        )

    init {
        Napier.d(tag = TAG) { "TrackViewModel initialized" }
        viewModelScope.launch {
            trackDataProvider.trackUiDataFlow().collect {
                Napier.d(tag = TAG) { "Track data updated: ${it.hashCode()}" }
                _state.value = it
            }
        }

        viewModelScope.launch {
            cancelLastAndRegisterUiSideEffect(force = false)
        }
    }

    fun onPullRefresh() {
        Napier.d(tag = TAG) { "onPullRefresh:" }
        cancelLastAndRegisterUiSideEffect(force = true)
    }

    fun onDeleteItem(item: MediaListModel) {
        viewModelScope.launch {
            val error =
                mediaRepository.updateMediaListStatus(
                    mediaListId = item.id,
                    status = MediaListStatus.DROPPED,
                )
            if (error != null) {
                Napier.e(tag = TAG) { "Failed to delete media list item ${item.id} $error" }

                submitError(error)
            }
        }
    }

    fun onMarkWatched(item: MediaListModel) {
        viewModelScope.launch {
            val error =
                mediaRepository.updateMediaListStatus(
                    mediaListId = item.id,
                    progress = (item.progress ?: 0) + 1,
                )
            if (error != null) {
                Napier.e(tag = TAG) { "Failed to onMarkWatched list item ${item.id} $error" }

                submitError(error)
            }
        }
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
                trackDataProvider
                    .trackUiSideEffect(forceRefreshFirstTime = force)
                    .collect { status ->
                        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect: sync status $status" }
                        isSideEffectRefreshing.value = status.isLoading()

                        submitErrorOfSyncStatus(status)
                    }
            }
    }

    fun onAuthIconClick(resultStore: ResultStore) {
        viewModelScope
            .launch {
                val result: LoginDialogResult = resultStore.awaitResultOf(Screen.Dialog.Login)
                when (result) {
                    LoginDialogResult.ClickLogin -> {
                        isLoginProcessing.value = true
                        val error = authRepository.startLoginProcessAndWaitResult()
                        if (error != null) {
                            submitError(error)
                        }
                    }

                    LoginDialogResult.ClickLogout -> {
                        authRepository.logout()
                    }
                }
            }.invokeOnCompletion {
                isLoginProcessing.value = false
            }
    }
}

@Composable
fun Track(
    modifier: Modifier = Modifier,
    navigator: RootNavigator = LocalRootNavigator.current,
    resultStore: ResultStore = LocalResultStore.current,
    viewModel: TrackViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isLoading.collectAsStateWithLifecycle()
    val appBarState by viewModel.appBarState.collectAsStateWithLifecycle()
    TrackContent(
        modifier = modifier,
        state = state,
        appbarState = appBarState,
        isRefreshing = isRefreshing,
        onPullRefresh = viewModel::onPullRefresh,
        onClickListItem = {
            navigator.navigateTo(Screen.DetailMedia(it.id))
        },
        onDeleteItem = viewModel::onDeleteItem,
        onMarkWatched = viewModel::onMarkWatched,
        onContentTypeChange = viewModel::changeContentMode,
        onAuthIconClick = {
            viewModel.onAuthIconClick(resultStore)
            navigator.navigateTo(Screen.Dialog.Login)
        },
        onSearchClick = {
            navigator.navigateTo(Screen.Search)
        },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrackContent(
    state: TrackUiState,
    appbarState: HomeAppBarUiState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onPullRefresh: () -> Unit = {},
    onClickListItem: (MediaModel) -> Unit = {},
    onDeleteItem: (MediaListModel) -> Unit = {},
    onMarkWatched: (MediaListModel) -> Unit = {},
    onContentTypeChange: (MediaContentMode) -> Unit = {},
    onAuthIconClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier =
            modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection),
        topBar = {
            DefaultAppBar(
                title = "Track",
                state = appbarState,
                scrollBehavior = appBarScrollBehavior,
                onContentTypeChange = onContentTypeChange,
                onAuthIconClick = onAuthIconClick,
                onSearchClick = onSearchClick,
            )
        },
    ) {
        CustomPullToRefresh(
            modifier =
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .background(color = AppBackgroundColor),
            isRefreshing = isRefreshing,
            onPullRefresh = onPullRefresh,
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                state.categoryWithItems.forEach { (category, items) ->
                    if (items.isNotEmpty()) {
                        stickyHeader(
                            key = "header_${category.name}",
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = AppBackgroundColor,
                            ) {
                                Text(
                                    modifier = Modifier.padding(top = 12.dp, start = 18.dp),
                                    text = category.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    shape = ShapeHelper.listItemShapeVertical(isFirst, isLast),
                                    titleMaxLines = Int.MAX_VALUE,
                                    userTitleLanguage = state.userOptions.titleLanguage,
                                    onClick = {
                                        onClickListItem(item.mediaModel)
                                    },
                                    onDelete = {
                                        onDeleteItem(item.mediaListModel)
                                    },
                                    onMarkWatched = {
                                        onMarkWatched(item.mediaListModel)
                                    },
                                )
                                Spacer(Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
