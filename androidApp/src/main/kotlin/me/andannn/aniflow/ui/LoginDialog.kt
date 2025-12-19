/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import io.github.andannn.LocalNavResultOwner
import io.github.andannn.NavResultOwner
import io.github.andannn.setNavResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.ui.widget.AlertDialogContainer
import me.andannn.aniflow.ui.widget.TransparentBackgroundListItem
import org.koin.compose.viewmodel.koinViewModel

class LoginDialogViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val state: StateFlow<UiState>
        field = MutableStateFlow(UiState())

    init {
        viewModelScope.launch {
            authRepository.getAuthedUserFlow().collect { user ->
                state.value = UiState(authedUser = user)
            }
        }
    }

    data class UiState(
        val authedUser: UserModel? = null,
    )
}

const val LOGIN_DIALOG_RESULT_KEY = "LoginDialogResultKey"

@Serializable
enum class LoginDialogResult {
    ClickLogin,
    ClickLogout,
}

@Composable
fun LoginDialog(
    viewModel: LoginDialogViewModel = koinViewModel(),
    navigator: RootNavigator = LocalRootNavigator.current,
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LoginDialogContent(
        state = state,
        onLoginClick = {
            navResultOwner.setNavResult(
                LOGIN_DIALOG_RESULT_KEY,
                LoginDialogResult.ClickLogin,
                LoginDialogResult.serializer(),
            )
            navigator.popBackStack()
        },
        onLogoutClick = {
            navResultOwner.setNavResult(
                LOGIN_DIALOG_RESULT_KEY,
                LoginDialogResult.ClickLogout,
                LoginDialogResult.serializer(),
            )
            navigator.popBackStack()
        },
        onNotificationClick = {
            navigator.popBackStack()
            navigator.navigateTo(Screen.Notification)
        },
        onSettingClick = {
            navigator.popBackStack()
            navigator.navigateTo(Screen.Settings)
        },
        onMyListClick = {
            navigator.popBackStack()
            navigator.navigateTo(Screen.MyList)
        },
    )
}

@Composable
fun LoginDialogContent(
    state: LoginDialogViewModel.UiState,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMyListClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val authedUser = state.authedUser
    AlertDialogContainer(
        modifier = modifier,
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
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp),
            )
        }

        Spacer(Modifier.height(4.dp))

        HorizontalDivider()

        if (authedUser != null) {
            TransparentBackgroundListItem(
                onClick = onNotificationClick,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                    )
                },
                headlineContent = {
                    Text("Notification")
                },
            )
            TransparentBackgroundListItem(
                onClick = onMyListClick,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Animation,
                        contentDescription = null,
                    )
                },
                headlineContent = {
                    Text("My list")
                },
            )
        }

        TransparentBackgroundListItem(
            onClick = onSettingClick,
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                )
            },
            headlineContent = {
                Text("Settings")
            },
        )

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
