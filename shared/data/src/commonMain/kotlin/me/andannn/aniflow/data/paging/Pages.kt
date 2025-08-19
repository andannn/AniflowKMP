package me.andannn.aniflow.data.paging

import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory
import org.koin.mp.KoinPlatform.getKoin

class MediaCategoryPageComponent(
    category: MediaCategory,
    config: PageConfig = DEFAULT_CONFIG,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        onLoadPage = { page, perPage ->
            mediaRepository
                .loadMediaPageByCategory(
                    category = category,
                    page = page,
                    perPage = perPage,
                )
        },
    )
