/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomPullToRefresh(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    enable: Boolean = true,
    onPullRefresh: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val scaleFraction = {
        if (isRefreshing) {
            1f
        } else {
            LinearOutSlowInEasing.transform(pullToRefreshState.distanceFraction).coerceIn(0f, 1f)
        }
    }
    Box(
        modifier =
            modifier.pullToRefresh(
                enabled = enable,
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = onPullRefresh,
            ),
    ) {
        content()

        Box(
            Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    scaleX = scaleFraction()
                    scaleY = scaleFraction()
                },
        ) {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
            )
        }
    }
}
