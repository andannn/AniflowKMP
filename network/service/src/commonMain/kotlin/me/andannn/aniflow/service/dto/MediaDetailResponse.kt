package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MediaDetailResponse(
    @SerialName(value = "Media")
    public val media: Media,
)
