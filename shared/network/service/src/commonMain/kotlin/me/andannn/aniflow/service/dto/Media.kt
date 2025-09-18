/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.MediaFormat
import me.andannn.aniflow.service.dto.enums.MediaSeason
import me.andannn.aniflow.service.dto.enums.MediaSource
import me.andannn.aniflow.service.dto.enums.MediaStatus
import me.andannn.aniflow.service.dto.enums.MediaType
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

@Serializable
public data class Media(
    /**
     * The id of the media
     */
    public val id: Int? = null,
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
    public val hashtag: String? = null,
    /**
     * If the media is marked as favourite by the current authenticated user
     */
    public val isFavourite: Boolean? = null,
    /**
     * External links to another site related to the media
     */
    public val externalLinks: List<ExternalLink?>? = null,
    /**
     * The ranking of the media in a particular time span and format compared to other media
     */
    public val rankings: List<Ranking?>? = null,
    /**
     * Media trailer or advertisement
     */
    public val trailer: Trailer? = null,
    /**
     * The cover images of the media
     */
    public val coverImage: CoverImage? = null,
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
     * Mean score of all the user's scores of the media
     */
    public val meanScore: Int? = null,
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
    public val nextAiringEpisode: NextAiringEpisode? = null,
    /**
     * The characters in the media
     */
    public val characters: CharactersConnection? = null,
    /**
     * The staff who produced the media
     */
    public val staff: StaffConnection? = null,
    /**
     * The companies who produced the media
     */
    public val studios: StudioConnection? = null,
    /**
     * Other media in the same or connecting franchise
     */
    public val relations: MediaRelations? = null,
)
