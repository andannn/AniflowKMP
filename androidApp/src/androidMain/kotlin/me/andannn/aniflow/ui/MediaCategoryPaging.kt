/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.stateIn
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.MediaCategoryPageComponent
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.title
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.util.SharedElementKey
import me.andannn.aniflow.ui.widget.CommonItemFilledCard
import me.andannn.aniflow.ui.widget.StaggeredGridPaging
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.LocalTopNavAnimatedContentScope
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "MediaCategoryPaging"

class MediaCategoryPagingViewModel(
    private val category: MediaCategory,
    private val authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    val pageComponent: PageComponent<MediaModel> =
        MediaCategoryPageComponent(category, errorHandler = this)

    init {
        Napier.d(tag = TAG) { "MediaCategoryPagingViewModel init. category: $category" }
    }

    val userOptionsFlow =
        authRepository.getUserOptionsFlow().stateIn(
            this.viewModelScope,
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5000),
            initialValue = UserOptions.Default,
        )

    override fun onCleared() {
        Napier.d(tag = TAG) { "MediaCategoryPagingViewModel cleared. category: $category" }
        pageComponent.dispose()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaCategoryPaging(
    category: MediaCategory,
    modifier: Modifier = Modifier,
    viewModel: MediaCategoryPagingViewModel =
        koinViewModel<MediaCategoryPagingViewModel>(
            parameters = { parametersOf(category) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val userOptions by viewModel.userOptionsFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier =
            modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarColors,
                title = {
                    Text(category.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
        StaggeredGridPaging(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(AppBackgroundColor),
            columns = StaggeredGridCells.Adaptive(160.dp),
            pageComponent = viewModel.pageComponent,
            contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
            key = { it.id },
        ) { item ->
            val title = item.title.getUserTitleString(titleLanguage = userOptions.titleLanguage)
            with(LocalSharedTransitionScope.current) {
                CommonItemFilledCard(
                    modifier =
                        Modifier.padding(4.dp).sharedBounds(
                            rememberSharedContentState(SharedElementKey.keyOfMediaItem(item)),
                            LocalTopNavAnimatedContentScope.current,
                        ),
                    title = title,
                    coverImage = item.coverImage,
                    onClick = {
                        navigator.navigateTo(Screen.DetailMedia(item.id))
                    },
                )
            }
        }
    }

    ErrorHandleSideEffect(viewModel)
}
