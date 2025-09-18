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
                    FontVariation.ascenderHeight(780f),
                    FontVariation.lowercaseHeight(448f),
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
                    FontVariation.thickStroke(86f),
                    FontVariation.counterWidth(435f),
                    FontVariation.uppercaseHeight(760f),
                    FontVariation.lowercaseHeight(437f),
                    FontVariation.ascenderHeight(854f),
                    FontVariation.descenderDepth(-350f),
                    FontVariation.figureHeight(700f),
                ),
        ),
    )

@OptIn(ExperimentalTextApi::class)
val StyledTitleFontFamily =
    FontFamily(
        Font(
            R.font.robotoflex_variable,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.slant(-9f),
                    FontVariation.width(25f),
                    FontVariation.weight(700),
                    FontVariation.grade(-66f),
                    FontVariation.counterWidth(446f),
                    FontVariation.thinStroke(77f),
                    FontVariation.ascenderHeight(791f),
                    FontVariation.descenderDepth(-305f),
                    FontVariation.figureHeight(788f),
                    FontVariation.lowercaseHeight(500f),
                    FontVariation.uppercaseHeight(760f),
                    FontVariation.opticalSize(true),
                ),
        ),
    )

@OptIn(ExperimentalTextApi::class)
val StyledReadingContentFontFamily =
    FontFamily(
        Font(
            R.font.roboto_serif_italic,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(400),
                    FontVariation.width(100f),
                ),
        ),
    )

private fun FontVariation.grade(value: Float) = FontVariation.Setting(name = "GRAD", value = value)

private fun FontVariation.thickStroke(value: Float) = FontVariation.Setting(name = "XOPQ", value = value)

private fun FontVariation.thinStroke(value: Float) = FontVariation.Setting(name = "YOPQ", value = value)

private fun FontVariation.counterWidth(value: Float) = FontVariation.Setting(name = "XTRA", value = value)

private fun FontVariation.uppercaseHeight(value: Float) = FontVariation.Setting(name = "YTUC", value = value)

private fun FontVariation.lowercaseHeight(value: Float) = FontVariation.Setting(name = "YTLC", value = value)

private fun FontVariation.ascenderHeight(value: Float) = FontVariation.Setting(name = "YTAS", value = value)

private fun FontVariation.descenderDepth(value: Float) = FontVariation.Setting(name = "YTDE", value = value)

private fun FontVariation.figureHeight(value: Float) = FontVariation.Setting(name = "YTFI", value = value)

private fun FontVariation.opticalSize(enabled: Boolean) = FontVariation.Setting(name = "opsz", value = if (enabled) 1f else 0f)
