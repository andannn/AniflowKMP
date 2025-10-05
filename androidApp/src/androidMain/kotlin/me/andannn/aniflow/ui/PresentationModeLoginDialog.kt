/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.ui

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable
import me.andannn.aniflow.ui.widget.AlertDialogContainer
import me.andannn.aniflow.util.LocalNavResultOwner
import me.andannn.aniflow.util.NavResultOwner
import me.andannn.aniflow.util.setNavResult

const val PRESENTATION_DIALOG_RESULT_KEY = "presentation_dialog_result_key"

@Serializable
object PresentationModeLoginAccepted

@Composable
fun PresentationModeLoginDialog(
    navResultOwner: NavResultOwner = LocalNavResultOwner.current,
    navigator: RootNavigator = LocalRootNavigator.current,
) {
    AlertDialogContainer {
        Text(
            "This build is for presentation and review purposes only.  \n" +
                "You may sign in with a test account to verify the app’s features.  \n" +
                "\n" +
                "Please tap the button below to log in.",
        )
        OutlinedButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                navigator.popBackStack()
                navResultOwner.setNavResult(
                    PRESENTATION_DIALOG_RESULT_KEY,
                    PresentationModeLoginAccepted,
                    PresentationModeLoginAccepted.serializer(),
                )
            },
        ) {
            Text("Login")
        }
    }
}
