/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data

import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.SearchCategory
import me.andannn.aniflow.data.model.StaffCharacterName
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.define.CharacterRole
import me.andannn.aniflow.data.model.define.MediaFormat
import me.andannn.aniflow.data.model.define.MediaRelation
import me.andannn.aniflow.data.model.define.MediaSeason
import me.andannn.aniflow.data.model.define.MediaSort
import me.andannn.aniflow.data.model.define.MediaSource
import me.andannn.aniflow.data.model.define.MediaStatus
import me.andannn.aniflow.data.model.define.MediaType
import me.andannn.aniflow.data.model.define.StaffLanguage
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun MediaModel.releasingTimeString(): String? {
    val timeUntilAiring = nextAiringEpisode?.timeUntilAiring
    if (nextAiringEpisode == null || timeUntilAiring == null) {
        return null
    }

    val airingTimeString = timeUntilAiring.seconds.formattedString()

    return airingTimeString
}

fun MediaModel.infoString(): String {
    val itemList = mutableListOf<String>()

    if (format != null) {
        var extra = ""
        if (type == MediaType.ANIME && source != null) {
            extra = "(${source.label()})"
        }
        itemList.add("${format.label()}$extra")
    }

    if (seasonYear != null) {
        itemList.add("$seasonYear")
    }

    if (season != null) {
        itemList.add(season.label())
    }

    if (episodes != null &&
        episodes != 0 &&
        (
            format == MediaFormat.MANGA ||
                format == MediaFormat.TV ||
                format == MediaFormat.OVA ||
                format == MediaFormat.ONA
        )
    ) {
        itemList.add("$episodes Ep")
    }

    if (status != null) {
        itemList.add(status.label())
    }

    return itemList.joinToString(" · ").ifEmpty { "----" }
}

fun Duration.formattedString(): String {
    val days = inWholeDays
    val hours = inWholeHours
    val minutes = inWholeMinutes

    return when {
        days > 0 -> "$days days"
        hours > 0 -> "$hours hours"
        minutes > 0 -> "$minutes minutes"
        else -> "0m"
    }
}

fun MediaRelation.label() =
    when (this) {
        MediaRelation.ADAPTATION -> "Adaptation"
        MediaRelation.PREQUEL -> "Prequel"
        MediaRelation.SEQUEL -> "Sequel"
        MediaRelation.PARENT -> "Parent Story"
        MediaRelation.SIDE_STORY -> "Side Story"
        MediaRelation.CHARACTER -> "Character"
        MediaRelation.SUMMARY -> "Summary"
        MediaRelation.ALTERNATIVE -> "Alternative Version"
        MediaRelation.SPIN_OFF -> "Spin-off"
        MediaRelation.OTHER -> "Other"
        MediaRelation.SOURCE -> "Source"
        MediaRelation.COMPILATION -> "Compilation"
        MediaRelation.CONTAINS -> "Contains"
    }

private fun MediaSource.label() =
    when (this) {
        MediaSource.ORIGINAL -> "Original"
        MediaSource.MANGA -> "Manga"
        MediaSource.LIGHT_NOVEL -> "Light novel"
        MediaSource.GAME -> "Game"
        MediaSource.OTHER -> "Other"
        MediaSource.VISUAL_NOVEL -> "Visual novel"
        MediaSource.VIDEO_GAME -> "Video game"
        MediaSource.NOVEL -> "Novel"
        MediaSource.DOUJINSHI -> "Doujinshi"
        MediaSource.ANIME -> "Anime"
        MediaSource.WEB_NOVEL -> "Web Novel"
        MediaSource.LIVE_ACTION -> "Live action"
        MediaSource.COMIC -> "Comic"
        MediaSource.MULTIMEDIA_PROJECT -> "Multimedia Project"
        MediaSource.PICTURE_BOOK -> "Picture book"
    }

fun MediaFormat.label() =
    when (this) {
        MediaFormat.TV -> "TV"
        MediaFormat.TV_SHORT -> "TV Short"
        MediaFormat.MOVIE -> "Movie"
        MediaFormat.SPECIAL -> "Special"
        MediaFormat.OVA -> "OVA"
        MediaFormat.ONA -> "ONA"
        MediaFormat.MUSIC -> "Music"
        MediaFormat.MANGA -> "Manga"
        MediaFormat.NOVEL -> "Novel"
        MediaFormat.ONE_SHOT -> "One shot"
    }

private fun MediaStatus.label() =
    when (this) {
        MediaStatus.FINISHED -> "Finished"
        MediaStatus.RELEASING -> "Releasing"
        MediaStatus.NOT_YET_RELEASED -> "Not yet released"
        MediaStatus.CANCELLED -> "Cancelled"
        MediaStatus.HIATUS -> "Hiatus"
    }

fun MediaSeason.label() =
    when (this) {
        MediaSeason.WINTER -> "Winter"
        MediaSeason.SPRING -> "Spring"
        MediaSeason.SUMMER -> "Summer"
        MediaSeason.FALL -> "Fall"
    }

fun SearchCategory.label() =
    when (this) {
        SearchCategory.ANIME -> "Anime"
        SearchCategory.MANGA -> "Manga"
        SearchCategory.CHARACTER -> "Character"
        SearchCategory.STAFF -> "Staff"
        SearchCategory.STUDIO -> "Studio"
    }

fun Title?.getUserTitleString(titleLanguage: UserTitleLanguage?): String {
    titleLanguage ?: return ""
    val title = this ?: return ""
    return when (titleLanguage) {
        UserTitleLanguage.ROMAJI -> title.romaji ?: title.english ?: title.native ?: ""
        UserTitleLanguage.ENGLISH -> title.english ?: title.romaji ?: title.native ?: ""
        UserTitleLanguage.NATIVE -> title.native ?: title.romaji ?: title.english ?: ""
    }
}

fun StaffCharacterName?.getNameString(staffName: UserStaffNameLanguage): String {
    if (this == null) return ""

    val romajiWestern =
        listOfNotNull(first, middle, last)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" ")
            ?.ifBlank { full }

    val romaji =
        listOfNotNull(last, first, middle)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" ")
            ?.ifBlank { full }

    return when (staffName) {
        UserStaffNameLanguage.ROMAJI_WESTERN -> {
            romajiWestern ?: romaji ?: native
        }

        UserStaffNameLanguage.ROMAJI -> {
            romaji ?: romajiWestern ?: native
        }

        UserStaffNameLanguage.NATIVE -> {
            native ?: romaji ?: romajiWestern
        }
    } ?: ""
}

fun MediaSort.label() =
    when (this) {
        MediaSort.START_DATE -> "Oldest"
        MediaSort.START_DATE_DESC -> "Newest"
        MediaSort.POPULARITY_DESC -> "Popularity"
        MediaSort.TRENDING_DESC -> "Average Score"
        MediaSort.FAVOURITES_DESC -> "Favorites"
    }

fun CharacterRole.label() =
    when (this) {
        CharacterRole.MAIN -> "Main"
        CharacterRole.SUPPORTING -> "Supporting"
        CharacterRole.BACKGROUND -> "Background"
    }

fun StaffLanguage.label() =
    when (this) {
        StaffLanguage.JAPANESE -> "Japanese"
        StaffLanguage.ENGLISH -> "English"
        StaffLanguage.KOREAN -> "Korean"
        StaffLanguage.ITALIAN -> "Italian"
        StaffLanguage.SPANISH -> "Spanish"
        StaffLanguage.PORTUGUESE -> "Portuguese"
        StaffLanguage.FRENCH -> "French"
        StaffLanguage.GERMAN -> "German"
        StaffLanguage.HEBREW -> "Hebrew"
        StaffLanguage.HUNGARIAN -> "Hungarian"
    }
