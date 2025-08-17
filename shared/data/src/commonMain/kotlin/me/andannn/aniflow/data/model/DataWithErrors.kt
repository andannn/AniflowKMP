/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import kotlinx.coroutines.flow.Flow

data class DataWithErrorFlow<T>(
    val dataFlow: Flow<T>,
    val errorFlow: Flow<Throwable>,
)

data class DataWithErrors<T>(
    val data: T? = null,
    val errors: List<Throwable> = emptyList(),
)
