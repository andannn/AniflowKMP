/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import io.github.andannn.LocalNavResultOwner
import io.github.andannn.NavResultOwner
import io.github.andannn.setNavResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.builtins.serializer
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.MediaRepository
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.ScoreFormat
import me.andannn.aniflow.ui.theme.AniflowTheme
import me.andannn.aniflow.ui.theme.EspecialMessageFontFamily
import me.andannn.aniflow.ui.widget.AlertDialogContainer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

private const val TAG = "ScoringDialog"

const val SCORE_DIALOG_RESULT = "SCORE_DIALOG_RESULT"

class ScoringDialogViewModel(
    private val mediaId: String,
    private val authRepository: AuthRepository,
    private val mediaRepository: MediaRepository,
) : ViewModel() {
    val scoreFormat =
        authRepository
            .getUserOptionsFlow()
            .map { it.scoreFormat }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserOptions.Default.scoreFormat,
            )

    val mediaListItem =
        flow {
            val user =
                authRepository.getAuthedUserFlow().firstOrNull() ?: error("no user logged in")
            val item =
                mediaRepository.getMediaListItemOfUserFlow(user.id, mediaId).first()
            emit(item)
        }.stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScoringDialog(
    mediaId: String,
    viewModel: ScoringDialogViewModel =
        koinViewModel(
            parameters = { parametersOf(mediaId) },
        ),
    navigator: RootNavigator = LocalRootNavigator.current,
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
) {
    val scoreFormat by viewModel.scoreFormat.collectAsStateWithLifecycle()
    val mediaListItem by viewModel.mediaListItem.collectAsStateWithLifecycle()

    AlertDialogContainer(
        title = "Scoring",
    ) {
        var score by remember(mediaListItem?.score) {
            mutableStateOf(mediaListItem?.score?.toFloat())
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            when (scoreFormat) {
                ScoreFormat.POINT_10_DECIMAL -> {
                    ScoringSlideBar(
                        currentScore = score,
                        valueRange = 0f..10f,
                        isDecimal = true,
                        onScoreChange = { score = it },
                    )
                }

                ScoreFormat.POINT_100 -> {
                    ScoringSlideBar(
                        currentScore = score,
                        valueRange = 0f..100f,
                        steps = 99,
                        isDecimal = false,
                        onScoreChange = { score = it.roundToInt().toFloat() },
                    )
                }

                ScoreFormat.POINT_10 -> {
                    ScoringSlideBar(
                        currentScore = score,
                        valueRange = 0f..10f,
                        steps = 9,
                        isDecimal = false,
                        onScoreChange = { score = it.roundToInt().toFloat() },
                    )
                }

                ScoreFormat.POINT_5 ->
                    StartScoreSelector(
                        currentScore = score,
                        onScoreChange = {
                            score = it.roundToInt().toFloat()
                        },
                    )

                ScoreFormat.POINT_3 ->
                    SmailFaceScoreSelector(
                        currentScore = score,
                        onScoreChange = {
                            score = it.roundToInt().toFloat()
                        },
                    )
            }
        }

        Row {
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = {
                    val scoreResult = score
                    if (scoreResult != null) {
                        navResultOwner.setNavResult(SCORE_DIALOG_RESULT, scoreResult, Float.serializer())
                    }

                    navigator.popBackStack()
                },
            ) {
                Text("Apply")
            }
            Spacer(Modifier.padding(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SmailFaceScoreSelector(
    modifier: Modifier = Modifier,
    currentScore: Float?,
    onScoreChange: (Float) -> Unit = {},
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        listOf(1f, 2f, 3f).forEach { option ->
            val isSelected = currentScore == option

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .size(72.dp) // 点击区域
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                Color.Transparent
                            },
                        ).clickable {
                            onScoreChange(option)
                        },
                verticalArrangement = Arrangement.Center,
            ) {
                val color =
                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else LocalContentColor.current
                Icon(
                    imageVector =
                        when (option) {
                            1f -> Icons.Filled.SentimentVeryDissatisfied
                            2f -> Icons.Filled.SentimentNeutral
                            3f -> Icons.Filled.SentimentVerySatisfied
                            else -> Icons.Filled.SentimentNeutral
                        },
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    text =
                        when (option) {
                            1f -> "Bad"
                            2f -> "Okay"
                            3f -> "Good"
                            else -> ""
                        },
                    style = MaterialTheme.typography.labelMediumEmphasized,
                    color = color,
                )
            }
        }
    }
}

@Composable
private fun StartScoreSelector(
    modifier: Modifier = Modifier,
    currentScore: Float?,
    onScoreChange: (Float) -> Unit = {},
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        listOf(1f, 2f, 3f, 4f, 5f).forEach { option ->
            val isSelected = (currentScore ?: 0f) >= option
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .clickable {
                            onScoreChange(option)
                        },
                verticalArrangement = Arrangement.Center,
            ) {
                val color =
                    if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                val icon =
                    if (isSelected) Icons.Default.Star else Icons.Default.StarOutline
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                )
            }
        }
    }
}

@Composable
private fun ScoringSlideBar(
    modifier: Modifier = Modifier,
    currentScore: Float?,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    isDecimal: Boolean = false,
    onScoreChange: (Float) -> Unit = {},
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        val score = currentScore ?: 0f
        val text = if (isDecimal) "%.1f".format(score) else "%d".format(score.roundToInt())
        if (currentScore != null) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = text,
                fontFamily = EspecialMessageFontFamily,
                fontSize = 20.sp,
            )
        } else {
            Text(text = "")
        }
        Slider(
            value = currentScore ?: 0f,
            valueRange = valueRange,
            steps = steps,
            onValueChange = { onScoreChange(it) },
        )
    }
}

@Preview
@Composable
private fun SmailFaceScoreSelectorPreview() {
    AniflowTheme {
        Surface {
            SmailFaceScoreSelector(
                currentScore = 2.0f,
                onScoreChange = {},
            )
        }
    }
}

@Preview
@Composable
private fun StartScoreSelectorPreview() {
    AniflowTheme {
        Surface {
            StartScoreSelector(
                currentScore = 2.0f,
                onScoreChange = {},
            )
        }
    }
}

@Preview
@Composable
private fun ScoringSlideBarPreview() {
    AniflowTheme {
        var score by remember {
            mutableStateOf(0f)
        }
        Surface {
            ScoringSlideBar(
                currentScore = score,
                valueRange = 0f..10f,
                steps = 9,
                onScoreChange = { score = it },
            )
        }
    }
}
