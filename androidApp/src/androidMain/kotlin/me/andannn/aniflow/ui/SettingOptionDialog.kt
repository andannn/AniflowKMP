/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import me.andannn.aniflow.data.model.SettingItem
import me.andannn.aniflow.data.model.SettingOption
import me.andannn.aniflow.util.LocalScreenResultEmitter
import me.andannn.aniflow.util.ScreenResultEmitter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TAG = "SettingOptionDialog"

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
    resultEmitter: ScreenResultEmitter = LocalScreenResultEmitter.current,
) {
    Surface(
        modifier =
            Modifier
                .wrapContentSize(),
        shape = AlertDialogDefaults.shape,
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = settingItem.title,
                color = AlertDialogDefaults.titleContentColor,
                style = MaterialTheme.typography.headlineSmallEmphasized,
            )
            when (settingItem) {
                is SettingItem.SingleSelect -> {
                    ToggleSettingOptionContent(
                        selected = viewModel.selectedOptions.firstOrNull(),
                        options = viewModel.options,
                        onOptionClick = {
                            resultEmitter.emitResult(it)
                            navigator.popBackStack()
                        },
                    )
                }
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
            ListItem(
                modifier =
                    Modifier.clickable {
                        onOptionClick(option)
                    },
                colors =
                    ListItemDefaults.colors(
                        containerColor = Color.Transparent,
                    ),
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
