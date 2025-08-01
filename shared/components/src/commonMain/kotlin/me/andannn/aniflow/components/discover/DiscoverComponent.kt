package me.andannn.aniflow.components.discover

import com.arkivanov.decompose.value.Value
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel

interface DiscoverComponent {
    val categoryDataMap: Value<Map<MediaCategory, List<MediaModel>>>
}
