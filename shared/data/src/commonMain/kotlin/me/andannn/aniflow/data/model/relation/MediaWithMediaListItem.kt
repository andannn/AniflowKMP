package me.andannn.aniflow.data.model.relation

import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel

data class MediaWithMediaListItem(
    val mediaModel: MediaModel,
    val mediaListModel: MediaListModel,
)
