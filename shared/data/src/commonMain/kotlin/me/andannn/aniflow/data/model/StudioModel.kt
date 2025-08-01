package me.andannn.aniflow.data.model

data class StudioModel(
    /**
     * The id of the studio
     */
    public val id: Int,
    /**
     * The name of the studio
     */
    public val name: String,
    /**
     * If the studio is an animation studio or a different kind of company
     */
    public val isAnimationStudio: Boolean,
    /**
     * The url for the studio page on the AniList website
     */
    public val siteUrl: String? = null,
    /**
     * If the studio is marked as favourite by the currently authenticated user
     */
    public val isFavourite: Boolean,
)
