/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.andannn.aniflow.R
import me.andannn.aniflow.data.model.define.MediaContentMode
import me.andannn.aniflow.ui.theme.AniflowTheme

@Composable
fun MediaContentSwitcher(
    modifier: Modifier = Modifier,
    mediaContent: MediaContentMode,
    onContentChange: (MediaContentMode) -> Unit = {},
) {
    val checked by rememberUpdatedState(
        mediaContent == MediaContentMode.ANIME,
    )

    val defaultColors = SwitchDefaults.colors()
    Switch(
        modifier =
            modifier.graphicsLayer(
                scaleX = 0.85f,
                scaleY = 0.85f,
            ),
        checked = checked,
        colors =
            SwitchDefaults.colors(
                uncheckedTrackColor = defaultColors.checkedTrackColor,
                uncheckedBorderColor = defaultColors.checkedTrackColor,
                uncheckedIconColor = MaterialTheme.colorScheme.primary,
                checkedIconColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                checkedThumbColor = MaterialTheme.colorScheme.surface,
            ),
        onCheckedChange = {
            onContentChange(
                if (it) {
                    MediaContentMode.ANIME
                } else {
                    MediaContentMode.MANGA
                },
            )
        },
        thumbContent = {
            Box(
                modifier = Modifier.padding(2.dp),
            ) {
                if (checked) {
                    Icon(
                        painterResource(R.drawable.outline_animation_24),
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        painterResource(R.drawable.outline_manga_24),
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Preview
@Composable
private fun CheckMediaContentSwitcherPreview() {
    AniflowTheme {
        Surface {
            MediaContentSwitcher(
                mediaContent = MediaContentMode.ANIME,
            )
        }
    }
}

@Preview
@Composable
private fun UncheckMediaContentSwitcherPreview2() {
    AniflowTheme {
        Surface {
            MediaContentSwitcher(
                mediaContent = MediaContentMode.MANGA,
            )
        }
    }
}
