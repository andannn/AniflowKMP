package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaType

enum class SearchCategory {
    ANIME,
    MANGA,
    CHARACTER,
    STAFF,
    STUDIO,
}

sealed class SearchSource(
    open val keyword: String? = null,
) {
    sealed class Media(
        override val keyword: String? = null,
        val type: MediaType,
    ) : SearchSource(keyword) {
        data class Anime(
            override val keyword: String? = null,
            val mediaFormat: List<MediaFormat>? = null,
            val seasonYear: String? = null,
            val season: MediaSeason? = null,
        ) : Media(keyword, MediaType.ANIME) {
            companion object {
                val None = Anime()
            }
        }

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
