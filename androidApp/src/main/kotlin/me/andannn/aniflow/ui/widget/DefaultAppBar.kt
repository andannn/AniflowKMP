/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.andannn.aniflow.data.model.HomeAppBarUiState
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.ui.theme.AppBackgroundColor
import me.andannn.aniflow.ui.theme.AppNameFontFamily
import me.andannn.aniflow.ui.theme.TopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    title: String,
    state: HomeAppBarUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    onContentTypeChange: (MediaContentMode) -> Unit = {},
    onAuthIconClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    val user = state.authedUser
    TopAppBar(
        scrollBehavior = scrollBehavior,
        colors = TopAppBarColors,
        title = {
            Text(
                text = title,
                fontFamily = AppNameFontFamily,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            MediaContentSwitcher(
                mediaContent = state.contentMode,
                onContentChange = onContentTypeChange,
            )
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatar(
                user = user,
                onAuthIconClick = onAuthIconClick,
            )
            Spacer(modifier = Modifier.width(12.dp))
        },
    )
}
