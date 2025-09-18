/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple80,
        secondary = PurpleGrey80,
        tertiary = Pink80,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40,
        /* Other default colors to override
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
         */
    )

object ShapeHelper {
    @Composable
    fun listItemShapeVertical(
        isFirst: Boolean,
        isLast: Boolean,
    ): RoundedCornerShape {
        val isSingle: Boolean = isFirst && isLast
        val edgeConerSize = MaterialTheme.shapes.large.topEnd
        val middleConerSize = MaterialTheme.shapes.extraSmall.topStart

        return if (isSingle) {
            RoundedCornerShape(
                topStart = edgeConerSize,
                topEnd = edgeConerSize,
                bottomStart = edgeConerSize,
                bottomEnd = edgeConerSize,
            )
        } else if (isFirst) {
            RoundedCornerShape(
                topStart = edgeConerSize,
                topEnd = edgeConerSize,
                bottomStart = middleConerSize,
                bottomEnd = middleConerSize,
            )
        } else if (isLast) {
            RoundedCornerShape(
                topStart = middleConerSize,
                topEnd = middleConerSize,
                bottomStart = edgeConerSize,
                bottomEnd = edgeConerSize,
            )
        } else {
            RoundedCornerShape(
                topStart = middleConerSize,
                topEnd = middleConerSize,
                bottomStart = middleConerSize,
                bottomEnd = middleConerSize,
            )
        }
    }

    @Composable
    fun listItemShapeHorizontal(
        isFirst: Boolean,
        isLast: Boolean,
    ): RoundedCornerShape {
        val isSingle: Boolean = isFirst && isLast
        val edgeCornerSize = MaterialTheme.shapes.large.topEnd
        val middleCornerSize = MaterialTheme.shapes.extraSmall.topStart

        return if (isSingle) {
            RoundedCornerShape(
                topStart = edgeCornerSize,
                bottomStart = edgeCornerSize,
                topEnd = edgeCornerSize,
                bottomEnd = edgeCornerSize,
            )
        } else if (isFirst) {
            RoundedCornerShape(
                topStart = edgeCornerSize,
                bottomStart = edgeCornerSize,
                topEnd = middleCornerSize,
                bottomEnd = middleCornerSize,
            )
        } else if (isLast) {
            RoundedCornerShape(
                topStart = middleCornerSize,
                bottomStart = middleCornerSize,
                topEnd = edgeCornerSize,
                bottomEnd = edgeCornerSize,
            )
        } else {
            RoundedCornerShape(
                topStart = middleCornerSize,
                bottomStart = middleCornerSize,
                topEnd = middleCornerSize,
                bottomEnd = middleCornerSize,
            )
        }
    }
}

val PageHorizontalPadding = 16.dp

val AppBackgroundColor
    @Composable
    get() = MaterialTheme.colorScheme.surfaceContainer

val TopAppBarColors
    @Composable
    get() =
        TopAppBarDefaults.topAppBarColors(
            containerColor = AppBackgroundColor,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AniflowTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val darkColorScheme = darkColorScheme(primary = Color(0xFF66ffc7))

    val colorScheme =
        when {
            supportsDynamicColor && isDarkTheme -> {
                dynamicDarkColorScheme(LocalContext.current)
            }

            supportsDynamicColor && !isDarkTheme -> {
                dynamicLightColorScheme(LocalContext.current)
            }

            isDarkTheme -> darkColorScheme
            else -> expressiveLightColorScheme()
        }

    val shapes = Shapes(largeIncreased = RoundedCornerShape(36.0.dp))

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        content = content,
    )
}
