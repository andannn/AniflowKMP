/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import io.github.andannn.LocalNavResultOwner
import io.github.andannn.NavResultOwner
import io.github.andannn.setNavResult
import me.andannn.aniflow.data.model.SettingItem
import me.andannn.aniflow.data.model.SettingOption
import me.andannn.aniflow.ui.widget.AlertDialogContainer
import me.andannn.aniflow.ui.widget.TransparentBackgroundListItem
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "SettingOptionDialog"

const val SETTING_OPTION_DIALOG_RESULT = "SETTING_OPTION_DIALOG_RESULT"

class SettingOptionViewModel(
    settingItem: SettingItem,
) : ViewModel() {
    init {
        Napier.d(tag = TAG) { "SettingOptionViewModel init. settingItem: $settingItem" }
    }

    val options =
        when (settingItem) {
            is SettingItem.SingleSelect -> settingItem.options
        }

    val selectedOptions: MutableList<SettingOption> =
        settingItem.selectedOptions().toMutableStateList()

    private fun SettingItem.selectedOptions() =
        when (this) {
            is SettingItem.SingleSelect -> listOf(this.selectedOption)
        }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingOptionDialog(
    settingItem: SettingItem,
    viewModel: SettingOptionViewModel =
        koinViewModel(
            parameters = { parametersOf(settingItem) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
) {
    AlertDialogContainer(
        title = settingItem.title,
    ) {
        when (settingItem) {
            is SettingItem.SingleSelect -> {
                ToggleSettingOptionContent(
                    selected = viewModel.selectedOptions.firstOrNull(),
                    options = viewModel.options,
                    onOptionClick = {
                        navResultOwner.setNavResult(SETTING_OPTION_DIALOG_RESULT, it, SettingOption.serializer())
                        navigator.popBackStack()
                    },
                )
            }
        }
    }
}

@Composable
private fun ToggleSettingOptionContent(
    modifier: Modifier = Modifier,
    options: List<SettingOption>,
    selected: SettingOption?,
    onOptionClick: (SettingOption) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        options.forEach { option ->
            TransparentBackgroundListItem(
                onClick = {
                    onOptionClick(option)
                },
                headlineContent = {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                trailingContent = {
                    RadioButton(
                        selected = option == selected,
                        onClick = null,
                    )
                },
            )
        }
    }
}
