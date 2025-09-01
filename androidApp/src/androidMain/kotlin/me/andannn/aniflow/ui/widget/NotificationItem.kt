/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.model.ActivityNotification
import me.andannn.aniflow.data.model.AiringNotification
import me.andannn.aniflow.data.model.FollowNotification
import me.andannn.aniflow.data.model.MediaDeletion
import me.andannn.aniflow.data.model.MediaModel
import me.andannn.aniflow.data.model.MediaNotification
import me.andannn.aniflow.data.model.NotificationModel
import me.andannn.aniflow.data.model.UserModel
import me.andannn.aniflow.ui.theme.AniflowTheme
import me.andannn.aniflow.ui.util.rememberUserTitle
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun NotificationItem(
    model: NotificationModel,
    modifier: Modifier = Modifier,
    onCoverImageClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
) {
    val relative =
        remember(model.createdAt) {
            val createAtSeconds = model.createdAt
            (Clock.System.now().epochSeconds - createAtSeconds).seconds.formattedString()
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onNotificationClick() },
    ) {
        // 右上角时间
        Text(
            text = relative,
            style = MaterialTheme.typography.labelSmall,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .alpha(0.7f),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(top = 8.dp, bottom = 8.dp),
        ) {
            // 左侧封面卡片（最小高度用右侧容器保证）
            Box(
                modifier =
                    Modifier
                        .width(85.dp)
                        .fillMaxHeight(),
            ) {
                Card(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .clickable { onCoverImageClick() },
                ) {
                    val coverUrl = getCoverImageUrl(model)
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 右侧内容：最小高度 85dp，垂直内边距 24dp，与 Flutter 版本一致
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 85.dp),
            ) {
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.CenterStart)
                            .padding(vertical = 24.dp),
                ) {
                    when (model) {
                        is AiringNotification ->
                            Text(
                                text = buildAiringText(notification = model),
                                style = MaterialTheme.typography.labelLarge,
                            )

                        is FollowNotification ->
                            Text(
                                text = buildFollowText(model),
                                style = MaterialTheme.typography.labelLarge,
                            )

                        is ActivityNotification ->
                            Text(
                                text = buildActivityText(model),
                                style = MaterialTheme.typography.labelLarge,
                            )

                        is MediaNotification ->
                            Text(
                                text = buildMediaText(model),
                                style = MaterialTheme.typography.labelLarge,
                            )

                        is MediaDeletion -> {
                            Text(
                                text = "MediaDeletionNotification",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
private fun buildAiringText(notification: AiringNotification): AnnotatedString =
    buildAnnotatedString {
        val contextList: List<String> = Json.decodeFromString(notification.context)
        append(contextList[0] + notification.episode + contextList[1])

        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            val title = rememberUserTitle(notification.media.title!!)
            append(title)
        }

        append(contextList[2])
    }

@Composable
private fun buildFollowText(n: FollowNotification): AnnotatedString =
    buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(n.user.name)
        }
        append(n.context)
    }

@Composable
private fun buildActivityText(n: ActivityNotification): AnnotatedString =
    buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            append(n.user.name)
        }
        append(n.context)
    }

@Composable
private fun buildMediaText(n: MediaNotification): AnnotatedString =
    buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            val title = rememberUserTitle(n.media.title!!)
            append(title)
        }
        append(n.context)
    }

fun Duration.formattedString(): String {
    val days = inWholeDays
    val hours = inWholeHours
    val minutes = inWholeMinutes

    return when {
        days > 0 -> "$days days"
        hours > 0 -> "$hours hours"
        minutes > 0 -> "$minutes minutes"
        else -> "0m"
    }
}

/**
 * —— 工具：从不同通知类型取封面图 ——
 */
private fun getCoverImageUrl(n: NotificationModel): String =
    when (n) {
        is AiringNotification -> n.media.coverImage.orEmpty()
        is FollowNotification -> n.user.avatar.orEmpty()
        is ActivityNotification -> n.user.avatar.orEmpty()
        is MediaNotification -> n.media.coverImage.orEmpty()
        is MediaDeletion -> ""
    }

@Preview
@Composable
private fun AiringNotificationPreview() {
    AniflowTheme {
        Surface {
            NotificationItem(
                model =
                    AiringNotification(
                        id = "1",
                        context =
                            "[\n" +
                                "            \"Episode \",\n" +
                                "            \" of \",\n" +
                                "            \" aired.\"\n" +
                                "          ]",
                        createdAt = 0,
                        episode = 1,
                        media =
                            MediaModel(
                                id = "1",
                                isFavourite = false,
                            ),
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun ActivityNotificationPreview() {
    AniflowTheme {
        Surface {
            NotificationItem(
                model =
                    ActivityNotification.Like(
                        id = "1",
                        context =
                            " liked your activity.",
                        createdAt = 0,
                        activityId = 1,
                        user =
                            UserModel(
                                id = "12",
                                name = "User name",
                            ),
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun FollowingNotificationPreview() {
    AniflowTheme {
        Surface {
            NotificationItem(
                model =
                    FollowNotification(
                        id = "1",
                        context = " Follow you.",
                        createdAt = 0,
                        user =
                            UserModel(
                                id = "12",
                                name = "User name",
                            ),
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun MediaNotificationPreview() {
    AniflowTheme {
        Surface {
            NotificationItem(
                model =
                    MediaNotification.RelatedMediaAddition(
                        id = "1",
                        context = " was recently added to the site.",
                        createdAt = 0,
                        media =
                            MediaModel(
                                id = "1",
                                isFavourite = false,
                            ),
                    ),
            )
        }
    }
}
