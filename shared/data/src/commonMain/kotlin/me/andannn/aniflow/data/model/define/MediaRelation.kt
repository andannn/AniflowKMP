/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * Type of relation media has to its parent.
 */
@Serializable
enum class MediaRelation {
    /**
     * An adaption of this media into a different format
     */
    ADAPTATION,

    /**
     * Released before the relation
     */
    PREQUEL,

    /**
     * Released after the relation
     */
    SEQUEL,

    /**
     * The media a side story is from
     */
    PARENT,

    /**
     * A side story of the parent media
     */
    SIDE_STORY,

    /**
     * Shares at least 1 character
     */
    CHARACTER,

    /**
     * A shortened and summarized version
     */
    SUMMARY,

    /**
     * An alternative version of the same media
     */
    ALTERNATIVE,

    /**
     * An alternative version of the media with a different primary focus
     */
    SPIN_OFF,

    /**
     * Other
     */
    OTHER,

    /**
     * Version 2 only. The source material the media was adapted from
     */
    SOURCE,

    /**
     * Version 2 only.
     */
    COMPILATION,

    /**
     * Version 2 only.
     */
    CONTAINS,
}
