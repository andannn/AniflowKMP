/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults.ExtraLargeContainerHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.andannn.LaunchNavResultHandler
import io.github.andannn.LocalNavResultOwner
import io.github.andannn.NavResultOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.HomeAppBarUiDataProvider
import me.andannn.aniflow.data.MarkProgressUseCase
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.TrackUiDataProvider
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.TrackUiState
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.data.submitErrorOfSyncStatus
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.ShapeHelper
import me.andannn.aniflow.ui.util.SharedElementKey
import me.andannn.aniflow.ui.util.buildSnackBarMessageHandler
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.DefaultAppBar
import me.andannn.aniflow.ui.widget.MediaRowItem
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.LocalSnackbarHostStateHolder
import me.andannn.aniflow.util.LocalTopNavAnimatedContentScope
import me.andannn.aniflow.util.SnackbarHostStateHolder
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
    val state =
        trackDataProvider
            .uiDataFlow()
            .stateIn(
                viewModelScope,
                initialValue = TrackUiState.Empty,
                started = SharingStarted.WhileSubscribed(5000),
            )

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
        viewModelScope.launch {
            cancelLastAndRegisterUiSideEffect(force = false)
        }
    }

    fun onPullRefresh() {
        Napier.d(tag = TAG) { "onPullRefresh:" }
        cancelLastAndRegisterUiSideEffect(force = true)
    }

    context(
        snackbarHost: SnackbarHostStateHolder,
    )
    fun onDeleteItem(item: MediaWithMediaListItem) {
        viewModelScope.launch {
            MarkProgressUseCase.markDropped(
                item,
                buildSnackBarMessageHandler(scope = this@launch, snackbarHost),
                this@TrackViewModel,
            )
        }
    }

    context(
        snackbarHost: SnackbarHostStateHolder,
    )
    fun onMarkClick(item: MediaWithMediaListItem) {
        viewModelScope.launch {
            MarkProgressUseCase.markProgress(
                item,
                (item.mediaListModel.progress ?: 0) + 1,
                buildSnackBarMessageHandler(scope = this@launch, snackbarHost),
                this@TrackViewModel,
            )
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
                    .uiSideEffect(forceRefreshFirstTime = force)
                    .collect { status ->
                        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect: sync status $status" }
                        isSideEffectRefreshing.value = status.isLoading()

                        submitErrorOfSyncStatus(status)
                    }
            }
    }

    fun onAuthLoginResult(result: LoginDialogResult) {
        viewModelScope
            .launch {
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

    fun startLoginProcess() {
        viewModelScope.launch {
            isLoginProcessing.value = true
            val error = authRepository.startLoginProcessAndWaitResult()
            if (error != null) {
                submitError(error)
            }
            isLoginProcessing.value = false
        }
    }
}

@Composable
fun Track(
    modifier: Modifier = Modifier,
    navigator: RootNavigator = LocalRootNavigator.current,
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
    snackbarHostState: SnackbarHostStateHolder = LocalSnackbarHostStateHolder.current,
    viewModel: TrackViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isLoading.collectAsStateWithLifecycle()
    val appBarState by viewModel.appBarState.collectAsStateWithLifecycle()

    LaunchNavResultHandler(LOGIN_DIALOG_RESULT_KEY, LoginDialogResult.serializer()) {
        viewModel.onAuthLoginResult(it)
    }

    context(navResultOwner, snackbarHostState) {
        TrackContent(
            modifier = modifier,
            state = state,
            appbarState = appBarState,
            isRefreshing = isRefreshing,
            onPullRefresh = viewModel::onPullRefresh,
            onClickListItem = {
                navigator.navigateTo(Screen.DetailMedia(it.id))
            },
            onDeleteItem = {
                viewModel.onDeleteItem(it)
            },
            onMarkWatched = {
                viewModel.onMarkClick(it)
            },
            onContentTypeChange = viewModel::changeContentMode,
            onAuthIconClick = {
                navigator.navigateTo(Screen.Dialog.Login)
            },
            onSearchClick = {
                navigator.navigateTo(Screen.Search)
            },
            onLogin = {
                viewModel.startLoginProcess()
            },
        )
    }

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
    onDeleteItem: (MediaWithMediaListItem) -> Unit = {},
    onMarkWatched: (MediaWithMediaListItem) -> Unit = {},
    onContentTypeChange: (MediaContentMode) -> Unit = {},
    onAuthIconClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onLogin: () -> Unit = {},
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
        if (state.authedUser == null) {
            LoginRequired(
                modifier =
                    Modifier
                        .padding(top = it.calculateTopPadding())
                        .background(color = AppBackgroundColor),
                onLoginClick = onLogin,
            )
        } else {
            CustomPullToRefresh(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = it.calculateTopPadding())
                        .background(color = AppBackgroundColor),
                isRefreshing = isRefreshing,
                onPullRefresh = onPullRefresh,
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
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
                                Column(
                                    modifier = Modifier.animateItem(),
                                ) {
                                    with(LocalSharedTransitionScope.current) {
                                        MediaRowItem(
                                            modifier =
                                                Modifier.fillMaxWidth().sharedBounds(
                                                    rememberSharedContentState(
                                                        SharedElementKey.keyOfMediaItem(item.mediaModel),
                                                    ),
                                                    LocalTopNavAnimatedContentScope.current,
                                                ),
                                            item = item,
                                            isScrollInProgress = listState.isScrollInProgress,
                                            shape =
                                                ShapeHelper.listItemShapeVertical(
                                                    isFirst,
                                                    isLast,
                                                ),
                                            titleMaxLines = Int.MAX_VALUE,
                                            userTitleLanguage = state.userOptions.titleLanguage,
                                            onClick = {
                                                onClickListItem(item.mediaModel)
                                            },
                                            onDelete = {
                                                onDeleteItem(item)
                                            },
                                            onMarkWatched = {
                                                onMarkWatched(item)
                                            },
                                        )
                                    }
                                    Spacer(Modifier.height(2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoginRequired(
    modifier: Modifier,
    onLoginClick: () -> Unit = {},
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            modifier = Modifier.height(ExtraLargeContainerHeight),
            onClick = onLoginClick,
            shapes =
                ButtonDefaults.shapes(
                    shape = ButtonDefaults.shape,
                    pressedShape = ButtonDefaults.extraLargePressedShape,
                ),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 64.dp),
                text = "Login",
                style = MaterialTheme.typography.headlineLargeEmphasized,
            )
        }
    }
}
