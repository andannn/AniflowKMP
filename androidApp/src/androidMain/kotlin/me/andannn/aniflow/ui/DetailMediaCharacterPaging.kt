/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.DetailMediaCharacterPageComponent
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.PageComponent
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.ui.widget.CharacterRowItem
import me.andannn.aniflow.ui.widget.VerticalListPaging
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "DetailMediaStaffPaging"

class DetailMediaCharacterPagingViewModel(
    private val mediaId: String,
    authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    private val _selectedLanguage = MutableStateFlow(StaffLanguage.JAPANESE)

    val selectedLanguage = _selectedLanguage.asStateFlow()
    var pagingController by
        mutableStateOf<PageComponent<CharacterWithVoiceActor>?>(null)

    val userOptionsFlow =
        authRepository.getUserOptionsFlow().stateIn(
            this.viewModelScope,
            started =
                SharingStarted
                    .WhileSubscribed(5000),
            initialValue = UserOptions.Default,
        )

    init {
        viewModelScope.launch {
            _selectedLanguage.collect { language ->
                Napier.d(tag = TAG) { "_selectedLanguage changed: $language" }
                pagingController?.dispose()
                pagingController =
                    DetailMediaCharacterPageComponent(
                        mediaId,
                        characterStaffLanguage = language,
                        errorHandler = this@DetailMediaCharacterPagingViewModel,
                    )
            }
        }
    }

    fun setSelectLanguage(language: StaffLanguage) {
        _selectedLanguage.value = language
    }

    override fun onCleared() {
        Napier.d(tag = TAG) { "DetailMediaCharacterPagingViewModel cleared. category: $mediaId" }
        pagingController?.dispose()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailMediaCharacterPaging(
    mediaId: String,
    modifier: Modifier = Modifier,
    viewModel: DetailMediaCharacterPagingViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
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
                    Text("Character")
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
                                Text(selectedLanguage.label())
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            StaffLanguage.entries.forEach {
                                DropdownMenuItem(
                                    text = { Text(it.label()) },
                                    onClick = {
                                        viewModel.setSelectLanguage(it)
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
        val pageController = viewModel.pagingController
        if (pageController != null) {
            VerticalListPaging(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(AppBackgroundColor),
                pageComponent = pageController,
                contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
                key = { index, _ -> index },
            ) { item ->
                CharacterRowItem(
                    modifier = Modifier.padding(4.dp),
                    shape = MaterialTheme.shapes.medium,
                    characterWithVoiceActor = item,
                    userStaffLanguage = userOptions.staffNameLanguage,
                    onStaffClick = {
                        navigator.navigateTo(
                            Screen.DetailStaff(it.id),
                        )
                    },
                    onCharacterClick = {
                        navigator.navigateTo(
                            Screen.DetailCharacter(it.id),
                        )
                    },
                )
            }
        }
    }

    ErrorHandleSideEffect(viewModel)
}

fun StaffLanguage.label() =
    when (this) {
        StaffLanguage.JAPANESE -> "Japanese"
        StaffLanguage.ENGLISH -> "English"
        StaffLanguage.KOREAN -> "Korean"
        StaffLanguage.ITALIAN -> "Italian"
        StaffLanguage.SPANISH -> "Spanish"
        StaffLanguage.PORTUGUESE -> "Portuguese"
        StaffLanguage.FRENCH -> "French"
        StaffLanguage.GERMAN -> "German"
        StaffLanguage.HEBREW -> "Hebrew"
        StaffLanguage.HUNGARIAN -> "Hungarian"
    }
