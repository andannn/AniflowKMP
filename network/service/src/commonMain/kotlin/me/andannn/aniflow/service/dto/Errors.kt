package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
data class AniListErrorResponse(
    val errors: List<Error>,
)

@Serializable
data class Error(
    val message: String,
    val status: Int,
)
