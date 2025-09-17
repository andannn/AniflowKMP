/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.ButtonWithIconContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.DetailMediaUiDataProvider
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.DetailUiState
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.submitErrorOfSyncStatus
import me.andannn.aniflow.ui.widget.CustomPullToRefresh
import me.andannn.aniflow.ui.widget.MenuItem
import me.andannn.aniflow.ui.widget.SplitDropDownMenuButton
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailMedia"

class DetailMediaViewModel(
    mediaId: String,
    private val dataProvider: DetailMediaUiDataProvider,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    init {
        viewModelScope.launch {
            cancelLastAndRegisterUiSideEffect(force = false)
        }
    }

    val isSideEffectRefreshing = MutableStateFlow(false)
    private var sideEffectJob: Job? = null

    val uiState =
        dataProvider.detailUiDataFlow().stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailUiState.Empty,
        )

    fun onPullRefresh() {
        Napier.d(tag = TAG) { "onPullRefresh:" }
        cancelLastAndRegisterUiSideEffect(force = true)
    }

    private fun cancelLastAndRegisterUiSideEffect(force: Boolean = false) {
        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect:" }
        sideEffectJob?.cancel()
        sideEffectJob =
            viewModelScope.launch {
                dataProvider
                    .detailUiSideEffect(forceRefreshFirstTime = force)
                    .collect { status ->
                        Napier.d(tag = TAG) { "cancelLastAndRegisterUiSideEffect: sync status $status" }
                        isSideEffectRefreshing.value = status.isLoading()

                        submitErrorOfSyncStatus(status)
                    }
            }
    }
}

@Composable
fun DetailMedia(
    mediaId: String,
    viewModel: DetailMediaViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isSideEffectRefreshing.collectAsStateWithLifecycle()

    DetailMediaContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        modifier = Modifier,
        onPullRefresh = { viewModel.onPullRefresh() },
        onPop = { navigator.popBackStack() },
    )

    ErrorHandleSideEffect(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DetailMediaContent(
    uiState: DetailUiState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    onPullRefresh: () -> Unit = {},
    onChangeStatus: (MediaListStatus) -> Unit = {},
    onPop: () -> Unit = {},
) {
    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(exitDirection = Bottom)
    Scaffold(
        modifier = modifier.nestedScroll(exitAlwaysScrollBehavior),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        bottomBar = {
        },
        topBar = {
            val colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )

            TopAppBar(
                colors = colors,
                title = {
                    Text(uiState.title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onPop()
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
        CustomPullToRefresh(
            modifier =
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
            isRefreshing = isRefreshing,
            onPullRefresh = onPullRefresh,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn {
                    item {
                        Text(uiState.mediaListItem.toString())
                    }
                    item {
                        Text(uiState.toString())
                    }
                }

                val authedUser = uiState.authedUser
                val mediaListItem = uiState.mediaListItem

                HorizontalFloatingToolbar(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                    scrollBehavior = exitAlwaysScrollBehavior,
                    expanded = true,
                    leadingContent = {
                        if (mediaListItem != null) {
                            val items = MediaListStatus.entries
                            SplitDropDownMenuButton(
                                menuItemList = MediaListStatus.entries.map { it.toMenuItem() },
                                selectIndex = items.indexOf(mediaListItem.status),
                                onMenuItemClick = {
                                    onChangeStatus(items[it])
                                },
                            )
                        } else {
                            Button(
                                colors =
                                    ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                contentPadding = ButtonWithIconContentPadding,
                                onClick = { /* doSomething() */ },
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Add to List")
                            }
                        }
                    },
                    content = {
                        if (authedUser == null) {
                            Button(
                                colors =
                                    ButtonDefaults.textButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                onClick = { /* doSomething() */ },
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Login")
                            }
                        }
                    },
                    trailingContent = {
                        AppBarRow(
                            overflowIndicator = { menuState ->
                                IconButton(
                                    onClick = {
                                        if (menuState.isExpanded) {
                                            menuState.dismiss()
                                        } else {
                                            menuState.show()
                                        }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Localized description",
                                    )
                                }
                            },
                        ) {
                            if (authedUser != null) {
                                clickableItem(
                                    onClick = { /* doSomething() */ },
                                    icon = {
                                        Icon(
                                            Icons.Outlined.FavoriteBorder,
                                            contentDescription = null,
                                        )
                                    },
                                    label = "Add to favorite",
                                )
                            }
                            if (authedUser != null && mediaListItem != null) {
                                clickableItem(
                                    onClick = { /* doSomething() */ },
                                    icon = {
                                        Icon(Icons.Filled.Bookmarks, contentDescription = null)
                                    },
                                    label = "Track Progress",
                                )
                                clickableItem(
                                    onClick = { /* doSomething() */ },
                                    icon = {
                                        Icon(Icons.Filled.StarRate, contentDescription = null)
                                    },
                                    label = "Give rating",
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

private fun MediaListStatus.toMenuItem() =
    when (this) {
        MediaListStatus.CURRENT ->
            MenuItem(
                label = "Watching",
                icon = Icons.Filled.PlayArrow, // 正在看 → 播放图标
            )
        MediaListStatus.PLANNING ->
            MenuItem(
                label = "Planning",
                icon = Icons.Filled.Schedule, // 计划中 → 时钟/日程图标
            )
        MediaListStatus.COMPLETED ->
            MenuItem(
                label = "Completed",
                icon = Icons.Filled.CheckCircle, // 完成 → 绿色对勾
            )
        MediaListStatus.DROPPED ->
            MenuItem(
                label = "Dropped",
                icon = Icons.Filled.Cancel, // 放弃 → 叉/禁用
            )
        MediaListStatus.PAUSED ->
            MenuItem(
                label = "Paused",
                icon = Icons.Filled.PauseCircle, // 暂停 → 暂停按钮
            )
        MediaListStatus.REPEATING ->
            MenuItem(
                label = "Repeating",
                icon = Icons.Filled.Repeat, // 重看 → 循环箭头
            )
    }
