package me.andannn.aniflow.data.model

public data class Title(
    /**
     * The romanization of the native language title
     */
    public val romaji: String? = null,
    /**
     * The official english title
     */
    public val english: String? = null,
    /**
     * Official title in it's native language
     */
    public val native: String? = null,
)
