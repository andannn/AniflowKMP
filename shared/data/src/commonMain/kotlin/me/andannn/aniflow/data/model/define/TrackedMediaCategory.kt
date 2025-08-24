package me.andannn.aniflow.data.model.define

import kotlinx.serialization.Serializable

@Serializable
enum class TrackedMediaCategory {
    ALL,
    NEW_AIRED,
    HAS_NEXT,
}
