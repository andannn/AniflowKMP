/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model

data class StudioModel(
    /**
     * The id of the studio
     */
    public val id: String,
    /**
     * The name of the studio
     */
    public val name: String,
    /**
     * If the studio is an animation studio or a different kind of company
     */
    public val isAnimationStudio: Boolean = false,
    /**
     * The url for the studio page on the AniList website
     */
    public val siteUrl: String? = null,
    /**
     * If the studio is marked as favourite by the currently authenticated user
     */
    public val isFavourite: Boolean,
)
