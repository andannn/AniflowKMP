package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class Title(
    /**
     * The romanization of the native language title
     */
    public val romaji: String?,
    /**
     * The official english title
     */
    public val english: String?,
    /**
     * Official title in it's native language
     */
    public val native: String?,
)
