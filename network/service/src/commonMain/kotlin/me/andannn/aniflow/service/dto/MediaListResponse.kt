package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaListResponse(
    @SerialName("MediaList")
    val mediaList: MediaList,
)
