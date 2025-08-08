package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.andannn.aniflow.components.common.paging.MediaCategoryPageComponent
import me.andannn.aniflow.ui.widget.MediaPreviewItem
import me.andannn.aniflow.ui.widget.VerticalGridPaging

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCategory(
    component: MediaCategoryPageComponent,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = component.category.toString()) },
            )
        },
    ) {
        VerticalGridPaging(
            modifier = Modifier.padding(it),
            columns = GridCells.Fixed(2),
            pageComponent = component,
            key = { it.id },
        ) { item ->
            MediaPreviewItem(
                modifier = Modifier,
                title = item.title?.english ?: "EEEEEEEEEE",
                isFollowing = false,
                coverImage = item.coverImage,
                ooClick = { },
            )
        }
    }
}
