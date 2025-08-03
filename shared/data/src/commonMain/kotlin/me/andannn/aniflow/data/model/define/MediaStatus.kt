/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

/**
 * The current releasing status of the media
 */
@Serializable
enum class MediaStatus {
    /**
     * Has completed and is no longer being released
     */
    FINISHED,

    /**
     * Currently releasing
     */
    RELEASING,

    /**
     * To be released at a later date
     */
    NOT_YET_RELEASED,

    /**
     * Ended before the work could be finished
     */
    CANCELLED,

    /**
     * Version 2 only. Is currently paused from releasing and will resume at a later date
     */
    HIATUS,
}
