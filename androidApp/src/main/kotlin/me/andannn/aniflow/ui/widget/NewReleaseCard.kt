/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.model.MediaListModel
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.Title
import me.andannn.aniflow.data.model.define.MediaListStatus
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.getUserTitleString
import me.andannn.aniflow.data.model.relation.MediaWithMediaListItem
import me.andannn.aniflow.ui.theme.AniflowTheme
import me.andannn.aniflow.ui.theme.EspecialMessageFontFamily
import me.andannn.aniflow.ui.theme.StyledTitleFontFamily
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewReleaseCard(
    modifier: Modifier = Modifier,
    items: List<MediaWithMediaListItem>,
    userTitleLanguage: UserTitleLanguage,
    onItemClick: (MediaWithMediaListItem) -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
    ) {
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 12.dp, bottom = 8.dp),
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
                                onItemClick(item)
                                animationScope.launch { state.animateScrollToItem(i) }
                            },
                    contentDescription = null,
                    model = item.mediaModel.bannerImage ?: item.mediaModel.coverImage,
                    contentScale = ContentScale.Crop,
                )
            }
            val title by rememberUpdatedState(
                currentItem.mediaModel.title.getUserTitleString(userTitleLanguage),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = StyledTitleFontFamily,
                fontSize = 24.sp,
            )

            Row {
                val text =
                    buildSpecialMessageText(
                        "Next up: Episode ${(currentItem.mediaListModel.progress ?: 0) + 1}",
                        MaterialTheme.colorScheme.primary,
                    )

                Text(text = text)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

fun buildSpecialMessageText(
    text: String,
    numberColor: Color,
    digitFontSize: TextUnit = 30.sp,
    textFontSize: TextUnit = 18.sp,
) = buildAnnotatedString {
    text.forEach {
        if (it.isDigit()) {
            withStyle(
                SpanStyle(
                    fontFamily = EspecialMessageFontFamily,
                    fontSize = digitFontSize,
                    color = numberColor,
                ),
            ) {
                append(it)
            }
        } else {
            withStyle(
                SpanStyle(
                    fontFamily = EspecialMessageFontFamily,
                    fontSize = textFontSize,
                ),
            ) {
                append(it)
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
            userTitleLanguage = UserTitleLanguage.ENGLISH,
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
                                status = MediaListStatus.DROPPED,
                            ),
                        airingScheduleUpdateTime = null,
                        firstAddedTime = null,
                    ),
                ),
        )
    }
}
