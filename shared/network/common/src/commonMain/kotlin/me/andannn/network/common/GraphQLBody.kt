/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common

import kotlinx.serialization.Serializable

@Serializable
data class GraphQLBody(
    val query: String,
    val variables: String,
)
