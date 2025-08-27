/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.model.UserModel
import org.koin.compose.viewmodel.koinViewModel

class LoginDialogViewModel(
    private val authRepository: AuthRepository,
    private val mediaRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    var loginJob: Job? = null

    init {
        viewModelScope.launch {
            authRepository.getAuthedUserFlow().collect { user ->
                _state.value = UiState(authedUser = user)
            }
        }
    }

    fun startLoginProcess() {
        loginJob?.cancel()

        loginJob =
            viewModelScope.launch {
                val error = authRepository.startLoginProcessAndWaitResult()
                // TODO: Handle the error
            }
    }

    fun logout() {
    }

    data class UiState(
        val authedUser: UserModel? = null,
    )
}

@Composable
fun LoginDialog(viewModel: LoginDialogViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navigator = LocalRootNavigator.current
    LoginDialogContent(
        state = state,
        onLoginClick = viewModel::startLoginProcess,
        onLogoutClick = {
            viewModel.logout()
            navigator.popBackStack()
        },
    )
}

@Composable
fun LoginDialogContent(
    state: LoginDialogViewModel.UiState,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val authedUser = state.authedUser
    Surface(
        modifier =
            modifier
                .wrapContentSize(),
        shape = AlertDialogDefaults.shape,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
            ) {
                IconButton(
                    onClick = { },
                ) {
                    if (authedUser != null) {
                        AsyncImage(
                            model = authedUser.avatar,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                        )
                    }
                }
                Text(
                    text = authedUser?.name ?: "",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp),
                )
            }

            HorizontalDivider()

            if (authedUser != null) {
                OutlinedButton(
                    onClick = onLogoutClick,
                ) {
                    Text("Logout")
                }
            } else {
                OutlinedButton(
                    onClick = onLoginClick,
                ) {
                    Text("Login with AniList")
                }
            }
        }
    }
}
