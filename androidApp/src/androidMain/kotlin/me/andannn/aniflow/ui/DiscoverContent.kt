package me.andannn.aniflow.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import me.andannn.aniflow.components.discover.DiscoverComponent

@Composable
fun Discover(
    component: DiscoverComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    DiscoverContent(
        state = state,
        modifier = modifier,
    )
}

@Composable
fun DiscoverContent(
    state: DiscoverComponent.DiscoverState,
    modifier: Modifier = Modifier,
) {
    Column {
        state.categoryDataMap.entries.forEach { entry ->
            Text(text = entry.key.toString(), modifier = modifier)
            Text(text = entry.value.toString(), modifier = modifier)
            HorizontalDivider()
        }
    }
}
