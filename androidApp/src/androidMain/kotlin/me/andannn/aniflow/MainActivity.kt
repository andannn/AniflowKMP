/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import io.github.aakira.napier.Napier
import me.andannn.aniflow.components.root.DefaultRootComponent
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.platform.BrowserAuthOperationHandlerImpl
import me.andannn.aniflow.ui.Root
import org.koin.android.ext.android.getKoin

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val browserAuthOperationHandler: BrowserAuthOperationHandlerImpl by lazy {
        getKoin().get<BrowserAuthOperationHandler>() as BrowserAuthOperationHandlerImpl
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        browserAuthOperationHandler.setUpContext(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val root =
            DefaultRootComponent(
                componentContext = defaultComponentContext(),
            )

        setContent {
            Root(root)
        }
    }

    override fun onResume() {
        super.onResume()
        Napier.d(tag = TAG) { "onResume" }
        browserAuthOperationHandler.onActivityResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        browserAuthOperationHandler.clearContext()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Napier.d(tag = TAG) { "onNewIntent $intent" }

        browserAuthOperationHandler.onReceiveNewIntent(intent)
    }
}
