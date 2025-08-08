package me.andannn.aniflow.components.common.paging

import com.arkivanov.decompose.ComponentContext
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.define.MediaCategory
import org.koin.mp.KoinPlatform.getKoin

interface MediaCategoryPageComponent : PageComponent<MediaModel> {
    val category: MediaCategory
}

class DefaultMediaCategoryPageComponent(
    componentContext: ComponentContext,
    override val category: MediaCategory,
    config: PageConfig = DEFAULT_CONFIG,
    private val mediaRepository: MediaRepository = getKoin().get(),
) : MediaCategoryPageComponent,
    PageComponent<MediaModel> by DefaultPageComponent(
        config = config,
        componentContext = componentContext,
        onLoadPage = { page, perPage ->
            mediaRepository
                .loadMediaPageByCategory(
                    category = category,
                    page = page,
                    perPage = perPage,
                )
        },
    )
