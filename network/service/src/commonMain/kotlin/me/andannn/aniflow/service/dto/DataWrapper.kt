package me.andannn.aniflow.service.dto

import kotlinx.serialization.Serializable

@Serializable
public data class DataWrapper<T>(
    public val data: T,
)
