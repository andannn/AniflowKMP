package me.andannn.aniflow.service.dto.enums

import kotlin.String

/**
 * The language the user wants to see media titles in
 */
enum class UserTitleLanguage(
    public val rawValue: String,
) {
    /**
     * The romanization of the native language title
     */
    ROMAJI("ROMAJI"),

    /**
     * The official english title
     */
    ENGLISH("ENGLISH"),

    /**
     * Official title in it's native language
     */
    NATIVE("NATIVE"),

    /**
     * The romanization of the native language title, stylised by media creator
     */
    ROMAJI_STYLISED("ROMAJI_STYLISED"),

    /**
     * The official english title, stylised by media creator
     */
    ENGLISH_STYLISED("ENGLISH_STYLISED"),

    /**
     * Official title in it's native language, stylised by media creator
     */
    NATIVE_STYLISED("NATIVE_STYLISED"),

    /**
     * Auto generated constant for unknown enum values
     */
    UNKNOWN__("UNKNOWN__"),
}
