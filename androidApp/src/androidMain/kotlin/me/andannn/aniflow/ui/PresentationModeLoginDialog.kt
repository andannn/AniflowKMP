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
import me.andannn.aniflow.ui.widget.AlertDialogContainer
import me.andannn.aniflow.util.LocalScreenResultEmitter
import me.andannn.aniflow.util.ScreenResultEmitter

object PresentationModeLoginAccepted

@Composable
fun PresentationModeLoginDialog(
    resultEmitter: ScreenResultEmitter = LocalScreenResultEmitter.current,
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
                resultEmitter.emitResult(PresentationModeLoginAccepted)
            },
        ) {
            Text("Login")
        }
    }
}
