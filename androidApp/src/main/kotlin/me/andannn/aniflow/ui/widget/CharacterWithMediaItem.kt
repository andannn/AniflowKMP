/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.data.getNameString
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.relation.VoicedCharacterWithMedia

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CharacterWithMediaItem(
    modifier: Modifier = Modifier,
    item: VoicedCharacterWithMedia,
    userTitleLanguage: UserTitleLanguage,
    userStaffLanguage: UserStaffNameLanguage,
    onCharacterClick: () -> Unit = {},
    onMediaClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = MaterialTheme.shapes.large,
        onClick = onCharacterClick,
    ) {
        Box(
            modifier =
                Modifier
                    .clip(MaterialTheme.shapes.large)
                    .aspectRatio(3f / 4f),
        ) {
            AsyncImage(
                modifier =
                    Modifier
                        .fillMaxSize(),
                model = item.character.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Surface(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth(0.4f)
                        .aspectRatio(3f / 4f),
                shape = RoundedCornerShape(topStart = 24.dp),
                onClick = onMediaClick,
            ) {
                AsyncImage(
                    model = item.media.coverImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp),
            text = item.character.name.getNameString(userStaffLanguage),
            style = MaterialTheme.typography.titleLargeEmphasized,
        )

        Text(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp),
            text = item.media.title.getUserTitleString(userTitleLanguage),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
        )

        Spacer(Modifier.height(12.dp))
    }
}
