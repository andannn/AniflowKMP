package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable
import me.andannn.aniflow.service.dto.enums.MediaListStatus

@Serializable
public data class MediaList(
    /**
     * The id of the list entry
     */
    public val id: Int,
    /**
     * The watching/reading status
     */
    public val status: MediaListStatus? = null,
    /**
     * The amount of episodes/chapters consumed by the user
     */
    public val progress: Int? = null,
    /**
     * Priority of planning
     */
    public val priority: Int? = null,
    /**
     * Text notes
     */
    public val notes: String? = null,
    /**
     * The amount of times the user has rewatched/read the media
     */
    public val repeat: Int? = null,
    /**
     * If the entry should only be visible to authenticated user
     */
    public val `private`: Boolean? = null,
    /**
     * The id of the user owner of the list entry
     */
    public val userId: Int,
    /**
     * When the entry data was last updated
     */
    public val updatedAt: Int? = null,
    /**
     * The score of the entry
     */
    public val score: Double? = null,
    /**
     * The amount of volumes read by the user
     */
    public val progressVolumes: Int? = null,
    /**
     * When the entry was started by the user
     */
    public val startedAt: FuzzyDate? = null,
    /**
     * When the entry was completed by the user
     */
    public val completedAt: FuzzyDate? = null,
    public val media: Media? = null,
)
