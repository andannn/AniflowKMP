/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class AniListErrorResponse(
    val data: String?,
    val errors: List<Error>,
)

@Serializable
data class Error(
    val message: String,
    val status: Int,
)
