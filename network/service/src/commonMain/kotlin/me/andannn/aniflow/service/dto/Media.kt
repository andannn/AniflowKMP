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
    public val id: Int,
    /**
     * The official titles of the media in various languages
     */
    public val title: Title?,
    /**
     * The type of the media; anime or manga
     */

    public val type: MediaType?,
    /**
     * Short description of the media's story and characters
     */

    public val description: String?,
    /**
     * The amount of episodes the anime has when complete
     */

    public val episodes: Int?,
    /**
     * The season year the media was initially released in
     */

    public val seasonYear: Int?,
    /**
     * The season the media was initially released in
     */

    public val season: MediaSeason?,
    /**
     * Source type the media was adapted from.
     */

    public val source: MediaSource?,
    /**
     * The genres of the media
     */

    public val genres: List<String?>?,
    /**
     * The current releasing status of the media
     */

    public val status: MediaStatus?,
    /**
     * Official Twitter hashtags for the media
     */
    public val hashtag: String?,
    /**
     * If the media is marked as favourite by the current authenticated user
     */
    public val isFavourite: Boolean,
    /**
     * External links to another site related to the media
     */
    public val externalLinks: List<ExternalLink?>?,
    /**
     * The ranking of the media in a particular time span and format compared to other media
     */
    public val rankings: List<Ranking?>?,
    /**
     * Media trailer or advertisement
     */
    public val trailer: Trailer?,
    /**
     * The cover images of the media
     */
    public val coverImage: CoverImage?,
    /**
     * The format the media was released in
     */
    public val format: MediaFormat?,
    /**
     * The banner image of the media
     */
    public val bannerImage: String?,
    /**
     * A weighted average score of all the user's scores of the media
     */
    public val averageScore: Int?,
    /**
     * The amount of user's who have favourited the media
     */
    public val favourites: Int?,
    /**
     * The amount of related activity in the past hour
     */
    public val trending: Int?,
    /**
     * The media's next episode airing schedule
     */
    public val nextAiringEpisode: NextAiringEpisode?,
    /**
     * The characters in the media
     */
    public val characters: Characters?,
    /**
     * The staff who produced the media
     */
    public val staff: StaffConnection?,
    /**
     * The companies who produced the media
     */
    public val studios: StudioConnection?,
)
