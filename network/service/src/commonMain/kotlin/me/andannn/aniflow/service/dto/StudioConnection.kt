package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class StudioConnection(
    public val nodes: List<Studio?>?,
)