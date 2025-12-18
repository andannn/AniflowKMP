/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ToggleFavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    IconButton(modifier = modifier, onClick = onClick) {
        if (isFavorite) {
            Icon(
                Icons.Outlined.Favorite,
                contentDescription = null,
                tint = Color.Red,
            )
        } else {
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = null,
            )
        }
    }
}
