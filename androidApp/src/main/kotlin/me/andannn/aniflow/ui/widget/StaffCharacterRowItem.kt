/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.data.label
import me.andannn.aniflow.data.model.CharacterModel
import me.andannn.aniflow.data.model.StaffModel
import me.andannn.aniflow.data.model.StaffWithRole
import me.andannn.aniflow.data.model.define.UserStaffNameLanguage
import me.andannn.aniflow.data.model.getNameString
import me.andannn.aniflow.data.model.relation.CharacterWithVoiceActor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StaffRowItem(
    shape: Shape,
    staffWithRole: StaffWithRole,
    userStaffLanguage: UserStaffNameLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier.height(90.dp),
        shape = shape,
        onClick = onClick,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                modifier = Modifier.width(72.dp),
                model = staffWithRole.staff.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.height(2.dp))
                val name =
                    remember(staffWithRole, userStaffLanguage) {
                        staffWithRole.staff.name.getNameString(userStaffLanguage)
                    }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    staffWithRole.role,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CharacterRowItem(
    characterWithVoiceActor: CharacterWithVoiceActor,
    shape: Shape,
    userStaffLanguage: UserStaffNameLanguage,
    modifier: Modifier = Modifier,
    onStaffClick: (StaffModel) -> Unit = {},
    onCharacterClick: (CharacterModel) -> Unit = {},
) {
    Surface(
        modifier = modifier.height(90.dp),
        shape = shape,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier =
                    Modifier.weight(1f).clickable {
                        onCharacterClick(characterWithVoiceActor.character)
                    },
            ) {
                AsyncImage(
                    modifier = Modifier.width(72.dp),
                    model = characterWithVoiceActor.character.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Spacer(modifier = Modifier.height(2.dp))
                    val name =
                        remember(characterWithVoiceActor, userStaffLanguage) {
                            characterWithVoiceActor.character.name.getNameString(userStaffLanguage)
                        }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        characterWithVoiceActor.role?.label() ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (characterWithVoiceActor.voiceActor != null) {
                Row(
                    modifier =
                        Modifier.weight(1f).clickable {
                            characterWithVoiceActor.voiceActor?.let {
                                onStaffClick(it)
                            }
                        },
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Spacer(modifier = Modifier.height(2.dp))
                        val name =
                            remember(characterWithVoiceActor, userStaffLanguage) {
                                characterWithVoiceActor.voiceActor?.name.getNameString(
                                    userStaffLanguage,
                                )
                            }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = characterWithVoiceActor.voiceActorLanguage.label(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    AsyncImage(
                        modifier = Modifier.width(72.dp),
                        model = characterWithVoiceActor.voiceActor?.image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
