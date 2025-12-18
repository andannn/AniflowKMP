/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.andannn.LaunchNavResultHandler
import io.github.andannn.LocalNavResultOwner
import io.github.andannn.NavResultOwner
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.ErrorChannel
import me.andannn.aniflow.data.SettingUiDataProvider
import me.andannn.aniflow.data.buildErrorChannel
import me.andannn.aniflow.data.model.SettingItem
import me.andannn.aniflow.data.model.SettingOption
import me.andannn.aniflow.data.model.SettingUiState
import me.andannn.aniflow.data.submitErrorOfSyncStatus
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.PageHorizontalPadding
import me.andannn.aniflow.ui.theme.ShapeHelper
import me.andannn.aniflow.ui.theme.TopAppBarColors
import me.andannn.aniflow.util.ErrorHandleSideEffect
import me.andannn.aniflow.util.rememberSnackBarHostState
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "Settings"

class SettingsViewModel(
    private val settingUiDataProvider: SettingUiDataProvider,
    private val authRepository: AuthRepository,
) : ViewModel(),
    ErrorChannel by buildErrorChannel() {
    val state =
        settingUiDataProvider.uiDataFlow().stateIn(
            scope = viewModelScope,
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5_000),
            initialValue = SettingUiState(),
        )

    init {
        viewModelScope.launch {
            settingUiDataProvider.uiSideEffect(forceRefreshFirstTime = true).collect {
                // Handle side effects if needed
                Napier.d(tag = TAG) { "Received side effect: $it" }

                submitErrorOfSyncStatus(it)
            }
        }
    }

    fun onSettingOptionSelected(option: SettingOption) {
        viewModelScope.launch {
            Napier.d(tag = TAG) { "On Option Click : $option" }
            handleChangeSetting(option)
        }
    }

    private suspend fun handleChangeSetting(option: SettingOption) {
        Napier.d(tag = TAG) { "handleChangeSetting: $option" }
        val error =
            when (option) {
                is SettingOption.StaffCharacterNameOption ->
                    authRepository.updateUserSettings(staffCharacterNameLanguage = option.value)

                is SettingOption.UserTitleLanguageOption ->
                    authRepository.updateUserSettings(titleLanguage = option.value)

                is SettingOption.ThemeModeOption ->
                    authRepository.updateUserSettings(appTheme = option.value)

                is SettingOption.ScoreFormatOption ->
                    authRepository.updateUserSettings(scoreFormat = option.value)
            }
        if (error != null) {
            Napier.e(tag = TAG) { "Failed to update setting: $error. option $option" }

            submitError(error)
        }
    }
}

@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    router: RootNavigator = LocalRootNavigator.current,
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
) {
    val state = settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchNavResultHandler(SETTING_OPTION_DIALOG_RESULT, SettingOption.serializer()) {
        settingsViewModel.onSettingOptionSelected(it)
    }

    with(navResultOwner) {
        SettingsContent(
            state = state.value,
            onPop = { router.popBackStack() },
            onSettingItemClick = { settingItem ->
                router.navigateTo(Screen.Dialog.SettingOption(settingItem))
            },
        )
    }

    ErrorHandleSideEffect(settingsViewModel)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingsContent(
    state: SettingUiState,
    modifier: Modifier = Modifier,
    onPop: () -> Unit = {},
    onSettingItemClick: (SettingItem) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(rememberSnackBarHostState()) },
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarColors,
                title = {
                    Text("Settings")
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
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(AppBackgroundColor)
                    .padding(it),
            contentPadding = PaddingValues(horizontal = PageHorizontalPadding),
        ) {
            state.settingGroupList.forEachIndexed { index, settingGroup ->
                if (settingGroup.settings.isNotEmpty()) {
                    stickyHeader(
                        key = settingGroup.title,
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AppBackgroundColor,
                        ) {
                            Text(
                                modifier = Modifier.padding(top = 12.dp, start = 18.dp),
                                text = settingGroup.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    itemsIndexed(
                        items = settingGroup.settings,
                        key = { index, item -> "${item.title}_$index" },
                    ) { index, setting ->
                        val isFirst = index == 0
                        val isLast = index == settingGroup.settings.lastIndex
                        val shape = ShapeHelper.listItemShapeVertical(isFirst, isLast)
                        SettingItem(
                            modifier = Modifier.padding(vertical = 1.dp),
                            setting = setting,
                            shape = shape,
                            onItemClick = {
                                onSettingItemClick(setting)
                            },
                        )
                    }

                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SettingItem(
    setting: SettingItem,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    onItemClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = shape,
        onClick = onItemClick,
    ) {
        ListItem(
            headlineContent = {
                Text(setting.title, style = MaterialTheme.typography.titleMediumEmphasized)
            },
            supportingContent = {
                if (setting is SettingItem.SingleSelect) {
                    Text(
                        setting.selectedOption.label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        )
    }
}
