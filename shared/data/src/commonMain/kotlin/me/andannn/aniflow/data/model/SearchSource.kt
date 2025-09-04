package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.MediaType

enum class SearchCategory {
    ANIME,
    MANGA,
    CHARACTER,
    STAFF,
    STUDIO,
}

sealed class SearchSource(
    open val keyword: String,
) {
    sealed class Media(
        override val keyword: String,
        val type: MediaType,
    ) : SearchSource(keyword) {
        data class Anime(
            override val keyword: String,
        ) : Media(keyword, MediaType.ANIME)

        data class Manga(
            override val keyword: String,
        ) : Media(keyword, MediaType.MANGA)
    }

    data class Character(
        override val keyword: String,
    ) : SearchSource(keyword)

    data class Staff(
        override val keyword: String,
    ) : SearchSource(keyword)

    data class Studio(
        override val keyword: String,
    ) : SearchSource(keyword)
}
