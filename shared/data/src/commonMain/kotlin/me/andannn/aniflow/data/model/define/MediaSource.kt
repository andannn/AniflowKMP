/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * Source type the media was adapted from
 */
@Serializable
enum class MediaSource {
    /**
     * An original production not based of another work
     */
    ORIGINAL,

    /**
     * Asian comic book
     */
    MANGA,

    /**
     * Written work published in volumes
     */
    LIGHT_NOVEL,

    /**
     * Video game driven primary by text and narrative
     */
    VISUAL_NOVEL,

    /**
     * Video game
     */
    VIDEO_GAME,

    /**
     * Other
     */
    OTHER,

    /**
     * Version 2+ only. Written works not published in volumes
     */
    NOVEL,

    /**
     * Version 2+ only. Self-published works
     */
    DOUJINSHI,

    /**
     * Version 2+ only. Japanese Anime
     */
    ANIME,

    /**
     * Version 3 only. Written works published online
     */
    WEB_NOVEL,

    /**
     * Version 3 only. Live action media such as movies or TV show
     */
    LIVE_ACTION,

    /**
     * Version 3 only. Games excluding video games
     */
    GAME,

    /**
     * Version 3 only. Comics excluding manga
     */
    COMIC,

    /**
     * Version 3 only. Multimedia project
     */
    MULTIMEDIA_PROJECT,

    /**
     * Version 3 only. Picture book
     */
    PICTURE_BOOK,
}
