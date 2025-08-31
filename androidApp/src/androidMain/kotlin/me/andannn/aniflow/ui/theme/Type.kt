/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.andannn.aniflow.R

// Set of Material typography styles to start with
val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
     */
    )

@OptIn(ExperimentalTextApi::class)
val AppNameFontFamily =
    FontFamily(
        Font(
            R.font.robotoflex_variable,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(741),
                    FontVariation.width(100f),
                    FontVariation.slant(-10f),
                    FontVariation.Setting("ytas", 780f),
                    FontVariation.Setting("ytlc", 448f),
                ),
        ),
    )

@OptIn(ExperimentalTextApi::class)
val EspecialMessageFontFamily =
    FontFamily(
        Font(
            R.font.robotoflex_variable,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.width(150f),
                    FontVariation.weight(350),
                    FontVariation.Setting("XOPQ", 86f),
                    FontVariation.Setting("XTRA", 435f),
                    FontVariation.Setting("YTUC", 760f),
                    FontVariation.Setting("YTLC", 437f),
                    FontVariation.Setting("YTAS", 854f),
                    FontVariation.Setting("YTDE", -350f),
                    FontVariation.Setting("YTFI", 700f),
                ),
        ),
    )
