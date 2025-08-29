package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.theme.AniflowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReleaseCard(
    modifier: Modifier = Modifier,
    items: List<MediaWithMediaListItem>,
) {
    OutlinedCard(
        modifier = modifier,
    ) {
        val state = rememberCarouselState { items.count() }
        val animationScope = rememberCoroutineScope()
        HorizontalCenteredHeroCarousel(
            state = state,
            modifier = Modifier.fillMaxWidth().height(221.dp).padding(horizontal = 24.dp),
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) { i ->
            val item = items[i]
            AsyncImage(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(205.dp)
                        .maskClip(MaterialTheme.shapes.extraLarge)
                        .clickable {
                            animationScope.launch { state.animateScrollToItem(i) }
                        },
                contentDescription = null,
                model = item.mediaModel.bannerImage,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
@Preview
private fun NewReleaseCardPreview() {
    AniflowTheme {
        NewReleaseCard(
            items = emptyList()
        )
    }
}
