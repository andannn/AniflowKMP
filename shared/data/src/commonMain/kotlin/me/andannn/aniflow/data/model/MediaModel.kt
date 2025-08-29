/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSource
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType

data class MediaModel(
    /**
     * The id of the media
     */
    public val id: String,
    /**
     * The official titles of the media in various languages
     */
    public val title: Title? = null,
    /**
     * The type of the media; anime or manga
     */
    public val type: MediaType? = null,
    /**
     * Short description of the media's story and characters
     */
    public val description: String? = null,
    /**
     * The amount of episodes the anime has when complete
     */
    public val episodes: Int? = null,
    /**
     * The season year the media was initially released in
     */
    public val seasonYear: Int? = null,
    /**
     * The season the media was initially released in
     */
    public val season: MediaSeason? = null,
    /**
     * Source type the media was adapted from.
     */
    public val source: MediaSource? = null,
    /**
     * The genres of the media
     */
    public val genres: List<String?>? = null,
    /**
     * The current releasing status of the media
     */
    public val status: MediaStatus? = null,
    /**
     * Official Twitter hashtags for the media
     */
    public val hashtag: List<String> = emptyList(),
    /**
     * If the media is marked as favourite by the current authenticated user
     */
    public val isFavourite: Boolean,
    /**
     * External links to another site related to the media
     */
    public val externalLinks: List<ExternalLink>? = emptyList(),
    public val ratedRank: Int? = null,
    public val popularRank: Int? = null,
    /**
     * Media trailer or advertisement
     */
    public val trailer: Trailer? = null,
    /**
     * The cover images of the media
     */
    public val coverImage: String? = null,
    /**
     * The format the media was released in
     */
    public val format: MediaFormat? = null,
    /**
     * The banner image of the media
     */
    public val bannerImage: String? = null,
    /**
     * A weighted average score of all the user's scores of the media
     */
    public val averageScore: Int? = null,
    /**
     * The amount of user's who have favourited the media
     */
    public val favourites: Int? = null,
    /**
     * The amount of related activity in the past hour
     */
    public val trending: Int? = null,
    /**
     * The media's next episode airing schedule
     */
    public val nextAiringEpisode: EpisodeModel? = null,
    public val siteUrl: String? = null,
)
