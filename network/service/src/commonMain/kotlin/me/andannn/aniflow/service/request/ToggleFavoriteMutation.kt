/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.request

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.DataWrapper
import me.andannn.network.common.schemas.TOGGLE_FAVORITE_MUTATION_SCHEMA

@Serializable
internal data class ToggleFavoriteMutation(
    val mediaId: Int? = null,
    val mangaId: Int? = null,
    val characterId: Int? = null,
    val staffId: Int? = null,
    val studioId: Int? = null,
) : GraphQLQuery<DataWrapper<Unit>> {
    override fun getSchema() = TOGGLE_FAVORITE_MUTATION_SCHEMA
}
