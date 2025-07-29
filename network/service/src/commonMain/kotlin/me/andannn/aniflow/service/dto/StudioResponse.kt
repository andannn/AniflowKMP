package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudioDetailResponse(
    @SerialName("Studio")
    val studio: Studio? = null,
)
