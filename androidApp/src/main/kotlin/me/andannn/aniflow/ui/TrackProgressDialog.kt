/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.andannn.LocalNavResultOwner
import io.github.andannn.NavResultOwner
import io.github.andannn.setNavResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.builtins.serializer
import me.andannn.aniflow.data.TrackProgressDialogDataProvider
import me.andannn.aniflow.data.model.TrackProgressDialogState
import me.andannn.aniflow.ui.widget.AlertDialogContainer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

const val TRACK_PROGRESS_DIALOG_RESULT = "TRACK_PROGRESS_DIALOG_RESULT"

class TrackProgressDialogViewModel(
    mediaId: String,
    dataProvider: TrackProgressDialogDataProvider,
) : ViewModel() {
    val uiData =
        dataProvider.uiDataFlow().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TrackProgressDialogState.Empty,
        )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TrackProgressDialog(
    mediaId: String,
    viewModel: TrackProgressDialogViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    val uiData by viewModel.uiData.collectAsStateWithLifecycle()

    AlertDialogContainer(
        title = "Track Progress",
    ) {
        TrackProgressDialogContent(
            initialProgress = uiData.initialProgress,
            maxEpisodes = uiData.maxEp,
        ) {
            navResultOwner.setNavResult(TRACK_PROGRESS_DIALOG_RESULT, it, Int.serializer())
            navigator.popBackStack()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TrackProgressDialogContent(
    modifier: Modifier = Modifier,
    initialProgress: Int,
    maxEpisodes: Int?,
    onSave: (Int) -> Unit = {},
) {
    var value by rememberSaveable(initialProgress) {
        mutableIntStateOf(initialProgress)
    }
    val hasNext = maxEpisodes != null && value < maxEpisodes
    val hasPrev = value > 0

    val focusRequester = remember { FocusRequester() }

    fun safeUpdateProgress(progress: Int) {
        if (maxEpisodes == null) value = progress

        value = progress.coerceIn(0, maxEpisodes)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            modifier = Modifier.height(56.dp),
            enabled = hasPrev,
            onClick = {
                safeUpdateProgress(value - 1)
            },
        ) {
            Text("−1")
        }

        OutlinedTextField(
            modifier =
                Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
            value =
                TextFieldValue(
                    text = value.toString(),
                    selection = TextRange(value.toString().length),
                ),
            onValueChange = { new ->
                safeUpdateProgress(new.text.toIntOrNull() ?: 0)
            },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            suffix = {
                if (maxEpisodes != null) {
                    Text(
                        "/$maxEpisodes",
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                    )
                }
            },
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        onSave(value)
                    },
                ),
        )

        OutlinedButton(
            enabled = hasNext,
            modifier = Modifier.height(56.dp),
            onClick = {
                safeUpdateProgress(value + 1)
            },
        ) { Text("+1") }
    }

    Spacer(Modifier.width(16.dp))
}
