package me.andannn.aniflow.service.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDetailResponse(
    @SerialName("Character")
    val character: Character? = null,
)
