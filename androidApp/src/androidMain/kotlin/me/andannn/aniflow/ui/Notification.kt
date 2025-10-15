/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.NotificationPageComponent
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.ActivityNotification
import me.andannn.aniflow.data.model.AiringNotification
import me.andannn.aniflow.data.model.FollowNotification
import me.andannn.aniflow.data.model.MediaDeletion
import me.andannn.aniflow.data.model.MediaNotification
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.FilterDropDownMenuButton
import me.andannn.aniflow.ui.widget.NotificationItem
import me.andannn.aniflow.ui.widget.VerticalListPaging
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "Notification"

class NotificationViewModel(
    authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    private val _selectedCategory = MutableStateFlow(NotificationCategory.ALL)

    val selectedCategory = _selectedCategory.asStateFlow()
    var pagingController by mutableStateOf<PageComponent<NotificationModel>?>(null)
    val userOptions =
        authRepository.getUserOptionsFlow().stateIn(
            viewModelScope,
            initialValue = UserOptions.Default,
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5000),
        )

    init {
        viewModelScope.launch {
            _selectedCategory.collect {
                Napier.d(tag = TAG) { "selectedCategory changed: $it" }
                pagingController?.dispose()
                pagingController =
                    NotificationPageComponent(it, errorHandler = this@NotificationViewModel)
            }
        }
    }

    fun selectCategory(category: NotificationCategory) {
        _selectedCategory.value = category
    }

    override fun onCleared() {
        pagingController?.dispose()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Notification(
    viewModel: NotificationViewModel = koinViewModel(),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val selected by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val userOptions by viewModel.userOptions.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarColors,
                title = {
                    Text("Notification")
                },
                actions = {
                    FilterDropDownMenuButton(
                        modifier =
                            Modifier
                                .padding(16.dp),
                        options = NotificationCategory.entries.map { it.label },
                        selectedIndex = NotificationCategory.entries.indexOf(selected),
                        onSelectIndex = { index ->
                            val category = NotificationCategory.entries.getOrNull(index)
                            if (category != null) viewModel.selectCategory(category)
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.popBackStack()
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
        NotificationPaging(
            modifier = Modifier.fillMaxSize().padding(it).background(AppBackgroundColor),
            pagingComponent = viewModel.pagingController,
            userTitleLanguage = userOptions.titleLanguage,
            onNotificationClick = { notification ->
                when (notification) {
                    is ActivityNotification -> {
                    }
                    is AiringNotification -> {
                        navigator.navigateTo(Screen.DetailMedia(notification.media.id))
                    }
                    is FollowNotification -> {
                    }
                    is MediaDeletion -> {
                    }
                    is MediaNotification -> {
                        navigator.navigateTo(Screen.DetailMedia(notification.media.id))
                    }
                }
            },
        )
    }

    ErrorHandleSideEffect(viewModel)
}

@Composable
fun NotificationPaging(
    pagingComponent: PageComponent<NotificationModel>?,
    modifier: Modifier = Modifier,
    userTitleLanguage: UserTitleLanguage,
    onNotificationClick: (NotificationModel) -> Unit = {},
) {
    if (pagingComponent != null) {
        VerticalListPaging(
            modifier = modifier,
            pageComponent = pagingComponent,
            contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
            key = { index, it -> it.id },
        ) { item ->
            NotificationItem(
                model = item,
                userTitleLanguage = userTitleLanguage,
                onNotificationClick = {
                    onNotificationClick(item)
                },
            )
        }
    }
}

private val NotificationCategory.label
    get() =
        when (this) {
            NotificationCategory.ALL -> "All"
            NotificationCategory.AIRING -> "Airing"
            NotificationCategory.ACTIVITY -> "Activity"
            NotificationCategory.FOLLOWS -> "Following"
            NotificationCategory.MEDIA -> "Media"
        }
