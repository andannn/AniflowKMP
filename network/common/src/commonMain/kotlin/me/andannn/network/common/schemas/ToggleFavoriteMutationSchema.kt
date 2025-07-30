/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.common.schemas

const val TOGGLE_FAVORITE_MUTATION_SCHEMA = $$"""
mutation ($animeId: Int, $mangaId: Int, $characterId: Int, $staffId: Int, $studioId: Int) {
  ToggleFavourite(animeId: $animeId, mangaId: $mangaId, characterId: $characterId, staffId: $staffId, studioId: $studioId) {
    anime {
      edges {
        id
      }
    }
  }
}
"""
