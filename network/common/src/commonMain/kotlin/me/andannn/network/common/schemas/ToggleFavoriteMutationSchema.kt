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
