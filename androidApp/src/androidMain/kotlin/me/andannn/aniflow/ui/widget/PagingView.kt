package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import me.andannn.aniflow.components.common.paging.LoadingStatus
import me.andannn.aniflow.components.common.paging.PageComponent

@Composable
fun <T> VerticalGridPaging(
    modifier: Modifier = Modifier,
    columns: GridCells,
    pageComponent: PageComponent<T>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
) {
    val items by pageComponent.items.subscribeAsState()
    val status by pageComponent.status.subscribeAsState()

    LazyVerticalGrid(
        modifier = modifier,
        columns = columns,
        contentPadding = contentPadding,
    ) {
        items(
            items = items,
            key = key,
        ) { item ->
            itemContent(item)
        }

        if (status is LoadingStatus.Loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }
    }
}
