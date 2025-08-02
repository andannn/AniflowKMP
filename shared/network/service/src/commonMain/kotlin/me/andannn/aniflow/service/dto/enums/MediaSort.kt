/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto.enums

import kotlinx.serialization.Serializable

/**
 * Media sort enums
 */
@Serializable
enum class MediaSort(
    public val rawValue: String,
) {
    ID("ID"),
    ID_DESC("ID_DESC"),
    TITLE_ROMAJI("TITLE_ROMAJI"),
    TITLE_ROMAJI_DESC("TITLE_ROMAJI_DESC"),
    TITLE_ENGLISH("TITLE_ENGLISH"),
    TITLE_ENGLISH_DESC("TITLE_ENGLISH_DESC"),
    TITLE_NATIVE("TITLE_NATIVE"),
    TITLE_NATIVE_DESC("TITLE_NATIVE_DESC"),
    TYPE("TYPE"),
    TYPE_DESC("TYPE_DESC"),
    FORMAT("FORMAT"),
    FORMAT_DESC("FORMAT_DESC"),
    START_DATE("START_DATE"),
    START_DATE_DESC("START_DATE_DESC"),
    END_DATE("END_DATE"),
    END_DATE_DESC("END_DATE_DESC"),
    SCORE("SCORE"),
    SCORE_DESC("SCORE_DESC"),
    POPULARITY("POPULARITY"),
    POPULARITY_DESC("POPULARITY_DESC"),
    TRENDING("TRENDING"),
    TRENDING_DESC("TRENDING_DESC"),
    EPISODES("EPISODES"),
    EPISODES_DESC("EPISODES_DESC"),
    DURATION("DURATION"),
    DURATION_DESC("DURATION_DESC"),
    STATUS("STATUS"),
    STATUS_DESC("STATUS_DESC"),
    CHAPTERS("CHAPTERS"),
    CHAPTERS_DESC("CHAPTERS_DESC"),
    VOLUMES("VOLUMES"),
    VOLUMES_DESC("VOLUMES_DESC"),
    UPDATED_AT("UPDATED_AT"),
    UPDATED_AT_DESC("UPDATED_AT_DESC"),
    SEARCH_MATCH("SEARCH_MATCH"),
    FAVOURITES("FAVOURITES"),
    FAVOURITES_DESC("FAVOURITES_DESC"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
