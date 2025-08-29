package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.theme.AniflowTheme
import me.andannn.aniflow.ui.util.rememberUserTitle
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewReleaseCard(
    modifier: Modifier = Modifier,
    items: List<MediaWithMediaListItem>,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "New Release",
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.tertiary,
                )

                Spacer(modifier = Modifier.weight(1f))
            }
            val state = rememberCarouselState { items.count() }
            val currentItem = items[state.currentItem]
            val animationScope = rememberCoroutineScope()
            HorizontalCenteredHeroCarousel(
                state = state,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                itemSpacing = 8.dp,
                minSmallItemWidth = 24.dp,
                maxSmallItemWidth = 28.dp,
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) { i ->
                val item = items[i]
                AsyncImage(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .maskClip(MaterialTheme.shapes.extraLarge)
                            .clickable {
                                animationScope.launch { state.animateScrollToItem(i) }
                            },
                    contentDescription = null,
                    model = item.mediaModel.bannerImage ?: item.mediaModel.coverImage,
                    contentScale = ContentScale.Crop,
                )
            }
            val title = rememberUserTitle(currentItem.mediaModel.title!!)
//            val title = "Title"
            Spacer(Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.titleLargeEmphasized)

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {},
                ) {
                    Text("Show all")
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun NewReleaseCardPreview() {
    AniflowTheme {
        NewReleaseCard(
            items =
                listOf(
                    MediaWithMediaListItem(
                        mediaModel =
                            MediaModel(
                                id = "1",
                                isFavourite = false,
                                title =
                                    Title(
                                        romaji = "",
                                        english = "Test",
                                        native = "",
                                    ),
                            ),
                        mediaListModel =
                            MediaListModel(
                                id = "1",
                            ),
                        airingScheduleUpdateTime = null,
                    ),
                ),
        )
    }
}
