package me.andannn.aniflow.components.common.paging

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.model.Page
import kotlin.coroutines.CoroutineContext

sealed interface LoadingStatus {
    object Idle : LoadingStatus

    object AllLoaded : LoadingStatus

    object Loading : LoadingStatus

    data class Error(
        val error: Throwable,
    ) : LoadingStatus
}

interface PageComponent<T> {
    val items: Value<List<T>>

    val status: Value<LoadingStatus>

    fun loadNextPage()
}

val DEFAULT_CONFIG =
    PageConfig(
        perPage = 20,
    )

data class PageConfig(
    val perPage: Int,
)

internal class DefaultPageComponent<T>(
    private val config: PageConfig,
    componentContext: ComponentContext,
    private val onLoadPage: suspend (page: Int, perPage: Int) -> Page<T>,
    mainContext: CoroutineContext = Dispatchers.Main,
) : PageComponent<T>,
    ComponentContext by componentContext {
    override val items = MutableValue<List<T>>(emptyList())

    override val status = MutableValue<LoadingStatus>(LoadingStatus.Idle)

    private val scope = coroutineScope(mainContext + SupervisorJob())

    init {
        scope.launch {
            status.value = LoadingStatus.Loading
            loadPage(1)
        }
    }

    override fun loadNextPage() {
        if (status.value is LoadingStatus.Loading || status.value is LoadingStatus.AllLoaded) {
            return
        }
        status.value = LoadingStatus.Loading
        scope.launch {
            val currentPage = (items.value.size / config.perPage) + 1
            loadPage(currentPage)
        }
    }

    private suspend fun loadPage(page: Int) {
        try {
            val page = onLoadPage(page, config.perPage)
            items.value = items.value + page.items
            if (page.hasNextPage) {
                status.value = LoadingStatus.Idle
            } else {
                status.value = LoadingStatus.AllLoaded
            }
        } catch (e: Exception) {
            status.value = LoadingStatus.Error(e)
        }
    }
}
