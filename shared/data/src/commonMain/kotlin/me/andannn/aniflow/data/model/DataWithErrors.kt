/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import kotlinx.coroutines.flow.Flow

data class DataWithError<T>(
    val data: T,
    val error: Throwable? = null,
)

data class DataWithErrors<T>(
    val data: T? = null,
    val errors: List<Throwable> = emptyList(),
)

data class DataAndErrorFlow<T>(
    val dataFlow: Flow<T>,
    val errorFlow: Flow<Throwable?>,
)
