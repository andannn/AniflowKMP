/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import me.andannn.aniflow.data.model.SimpleDate

private fun SimpleDate.toLocalDate(): LocalDate? = day?.let { LocalDate(year, month, it) }

fun SimpleDate.format(): String =
    toLocalDate()?.format(LocalDate.Formats.ISO)
        ?: "%04d-%02d".format(year, month)
