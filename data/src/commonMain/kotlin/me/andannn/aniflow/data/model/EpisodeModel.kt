package me.andannn.aniflow.data.model

data class EpisodeModel(
    /**
     * The id of the airing schedule item
     */
    public val id: Int,
    /**
     * The time the episode airs at
     */
    public val airingAt: Int,
    /**
     * The airing episode number
     */
    public val episode: Int,
    /**
     * Seconds until episode starts airing
     */
    public val timeUntilAiring: Int,
)
