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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import kotlinx.coroutines.flow.stateIn
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DetailMediaStaffPaging
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.StaffWithRole
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.StaffRowItem
import me.andannn.aniflow.ui.widget.VerticalListPaging
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailMediaStaffPaging"

class DetailMediaStaffPagingViewModel(
    private val mediaId: String,
    authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    val pageComponent: PageComponent<StaffWithRole> =
        DetailMediaStaffPaging(mediaId, errorHandler = this)

    val userOptionsFlow =
        authRepository.getUserOptionsFlow().stateIn(
            this.viewModelScope,
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5000),
            initialValue = UserOptions(),
        )

    override fun onCleared() {
        Napier.d(tag = TAG) { "MediaCategoryPagingViewModel cleared. category: $mediaId" }
        pageComponent.dispose()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailMediaStaffPaging(
    mediaId: String,
    modifier: Modifier = Modifier,
    viewModel: DetailMediaStaffPagingViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val userOptions by viewModel.userOptionsFlow.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarColors,
                title = {
                    Text("Staff")
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
        VerticalListPaging(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(AppBackgroundColor),
            pageComponent = viewModel.pageComponent,
            contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
            key = { index, _ -> index },
        ) { item ->
            StaffRowItem(
                modifier = Modifier.padding(4.dp),
                shape = MaterialTheme.shapes.medium,
                staffWithRole = item,
                userStaffLanguage = userOptions.staffNameLanguage,
            )
        }
    }

    ErrorHandleSideEffect(viewModel)
}
