/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.data.getUserTitleString
import me.andannn.aniflow.data.infoString
import me.andannn.aniflow.data.model.define.MediaRelation
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import me.andannn.aniflow.data.model.relation.MediaModelWithRelationType

@Composable
fun MediaRelationItem(
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    userTitleLanguage: UserTitleLanguage,
    mediaRelation: MediaModelWithRelationType,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier =
            modifier
                .height(120.dp)
                .widthIn(min = 200.dp, max = 300.dp),
        shape = shape,
        onClick = onClick,
    ) {
        Row {
            AsyncImage(
                modifier =
                    Modifier
                        .aspectRatio(3f / 4f),
                model = mediaRelation.media.coverImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mediaRelation.relationType.label(),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = mediaRelation.media.title.getUserTitleString(userTitleLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.weight(3f))
                val info =
                    remember(mediaRelation.media) {
                        mediaRelation.media.infoString()
                    }
                val colorScheme = MaterialTheme.colorScheme
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.onSurfaceVariant),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

private fun MediaRelation.label() =
    when (this) {
        MediaRelation.ADAPTATION -> "Adaptation"
        MediaRelation.PREQUEL -> "Prequel"
        MediaRelation.SEQUEL -> "Sequel"
        MediaRelation.PARENT -> "Parent Story"
        MediaRelation.SIDE_STORY -> "Side Story"
        MediaRelation.CHARACTER -> "Character"
        MediaRelation.SUMMARY -> "Summary"
        MediaRelation.ALTERNATIVE -> "Alternative Version"
        MediaRelation.SPIN_OFF -> "Spin-off"
        MediaRelation.OTHER -> "Other"
        MediaRelation.SOURCE -> "Source"
        MediaRelation.COMPILATION -> "Compilation"
        MediaRelation.CONTAINS -> "Contains"
    }
