/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.rememberNavBackStack
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import me.andannn.aniflow.data.AuthRepository
import me.andannn.aniflow.data.BrowserAuthOperationHandler
import me.andannn.aniflow.data.model.UserOptions
import me.andannn.aniflow.data.model.define.Theme
import me.andannn.aniflow.platform.BrowserAuthOperationHandlerImpl
import me.andannn.aniflow.ui.App
import me.andannn.aniflow.ui.DeepLinkHelper
import me.andannn.aniflow.ui.RootNavigator
import me.andannn.aniflow.ui.Screen
import me.andannn.aniflow.ui.theme.AniflowTheme
import me.andannn.aniflow.util.LocalResultStore
import me.andannn.aniflow.util.ResultStore
import me.andannn.aniflow.worker.SyncWorkHelper
import org.koin.android.ext.android.getKoin
import org.koin.compose.getKoin

private val runTimePermissions =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyList()
    }

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val browserAuthOperationHandler: BrowserAuthOperationHandlerImpl by lazy {
        getKoin().get<BrowserAuthOperationHandler>() as BrowserAuthOperationHandlerImpl
    }

    private val authRepository: AuthRepository by lazy { getKoin().get() }

    private val paddingDeepLinkNavigationScreen = mutableStateOf<Screen?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        browserAuthOperationHandler.setUpContext(this)
        lifecycleScope.launch {
            authRepository.getUserOptionsFlow().collect { option ->
                when (option.appTheme) {
                    Theme.DARK -> enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))
                    Theme.LIGHT ->
                        enableEdgeToEdge(
                            statusBarStyle =
                                SystemBarStyle.light(
                                    scrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT,
                                ),
                        )

                    else -> enableEdgeToEdge()
                }
            }
        }

        // Ensure the periodic sync worker is registered
        SyncWorkHelper.registerPeriodicSyncWork(this)

        super.onCreate(savedInstanceState)

        // handle deep link navigation
        paddingDeepLinkNavigationScreen.value = DeepLinkHelper.parseUri(intent.data.toString())

        setContent {
            var permissionGranted by remember {
                mutableStateOf(isPermissionGranted())
            }
            val launcher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = {
                        it.forEach { (permission, granted) ->
                            if (!granted) {
                                Napier.d(tag = TAG) { "permission $permission not granted." }
                            }
                        }
                        permissionGranted = true
                    },
                )

            if (!permissionGranted) {
                LaunchedEffect(Unit) {
                    runTimePermissions
                        .filter {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                it,
                            ) == PackageManager.PERMISSION_DENIED
                        }.let {
                            launcher.launch(it.toTypedArray())
                        }
                }
            }

            val appTheme = rememberAppThemeSetting()
            val isDarkMode =
                when (appTheme) {
                    Theme.SYSTEM -> isSystemInDarkTheme()
                    Theme.LIGHT -> false
                    Theme.DARK -> true
                }
            AniflowTheme(isDarkTheme = isDarkMode) {
                val backStack = rememberNavBackStack<Screen>(Screen.Home)
                val navigator = remember(backStack) { RootNavigator(backStack) }

                LaunchedEffect(paddingDeepLinkNavigationScreen.value) {
                    paddingDeepLinkNavigationScreen.value?.let {
                        navigator.navigateTo(it)
                        paddingDeepLinkNavigationScreen.value = null
                    }
                }
                val resultStore = remember { ResultStore() }
                CompositionLocalProvider(
                    LocalResultStore provides resultStore,
                ) {
                    App(navigator)
                }
            }
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

        Napier.d(tag = TAG) { "onNewIntent ${intent.data}" }

        browserAuthOperationHandler.onReceiveNewIntent(intent)

        paddingDeepLinkNavigationScreen.value = DeepLinkHelper.parseUri(intent.data.toString())
    }

    private fun isPermissionGranted(): Boolean {
        runTimePermissions.forEach { permission ->
            when (ContextCompat.checkSelfPermission(this, permission)) {
                PackageManager.PERMISSION_DENIED -> return false
            }
        }
        return true
    }
}

@Composable
private fun rememberAppThemeSetting(authRepository: AuthRepository = getKoin().get()): Theme {
    val option = authRepository.getUserOptionsFlow().collectAsState(UserOptions())
    return option.value.appTheme
}
