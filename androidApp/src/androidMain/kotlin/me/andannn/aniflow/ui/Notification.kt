/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.NotificationCategory
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.paging.NotificationPageComponent
import me.andannn.aniflow.data.paging.PageComponent
import me.andannn.aniflow.ui.widget.NotificationItem
import me.andannn.aniflow.ui.widget.VerticalListPaging
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "Notification"

class NotificationViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow(NotificationCategory.ALL)

    val selectedCategory = _selectedCategory.asStateFlow()
    var pagingController by mutableStateOf<PageComponent<NotificationModel>?>(null)
    val userOptions =
        authRepository.getUserOptionsFlow().stateIn(
            viewModelScope,
            initialValue = UserOptions(),
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5000),
        )

    init {
        viewModelScope.launch {
            _selectedCategory.collect {
                Napier.d(tag = TAG) { "selectedCategory changed: $it" }
                pagingController?.dispose()
                pagingController = NotificationPageComponent(it)
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
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text("Notification")
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier =
                            Modifier
                                .padding(16.dp),
                    ) {
                        TextButton(onClick = { expanded = !expanded }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.FilterAlt, contentDescription = "Filter")
                                Spacer(Modifier.width(8.dp))
                                Text(selected.label)
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            NotificationCategory.entries.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.label) },
                                    onClick = {
                                        viewModel.selectCategory(it)
                                        expanded = false
                                    },
                                )
                            }
                        }
                    }
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
            modifier = Modifier.padding(it),
            pagingComponent = viewModel.pagingController,
            userTitleLanguage = userOptions.titleLanguage,
        )
    }
}

@Composable
fun NotificationPaging(
    pagingComponent: PageComponent<NotificationModel>?,
    modifier: Modifier = Modifier,
    userTitleLanguage: UserTitleLanguage,
) {
    if (pagingComponent != null) {
        VerticalListPaging(
            modifier = modifier,
            pageComponent = pagingComponent,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            key = { it.id },
        ) { item ->
            NotificationItem(
                model = item,
                userTitleLanguage = userTitleLanguage,
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
