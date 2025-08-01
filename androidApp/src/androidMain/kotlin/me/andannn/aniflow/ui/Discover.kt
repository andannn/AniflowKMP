package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.Value
import me.andannn.aniflow.components.discover.DiscoverComponent
import me.andannn.aniflow.data.model.MediaCategory
import me.andannn.aniflow.data.model.MediaModel

@Composable
fun Discover(
    component: DiscoverComponent,
    modifier: Modifier = Modifier,
) {
    val categoryDataMap by component.categoryDataMap.subscribeAsState()
    DiscoverContent(
        categoryDataMap = categoryDataMap,
        modifier = modifier,
    )
}

@Composable
fun DiscoverContent(
    categoryDataMap: Map<MediaCategory, List<MediaModel>>,
    modifier: Modifier = Modifier,
) {
    Column {
        categoryDataMap.entries.forEach { entry ->
            Text(text = entry.key.toString(), modifier = modifier)
            Text(text = entry.value.toString(), modifier = modifier)
            HorizontalDivider()
        }
    }
}
