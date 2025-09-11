package me.andannn.aniflow.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.navEntryDecorator
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import me.andannn.aniflow.ui.Screen

private const val TAG = "ResultStore"

class ResultStore {
    private val map = mutableMapOf<Screen, MutableSharedFlow<Any?>>()

    suspend fun awaitResultOf(screen: Screen): Any? {
        Napier.d(tag = TAG) { "awaiting resultOf screen $screen" }
        return flow.firstOrNull()
    }

    fun <T> emit(
        screen: Screen,
        value: T,
    ) {
        Napier.d(tag = TAG) { "emit $value" }
        val flow = map.getOrPut(screen) { MutableSharedFlow<T?>(extraBufferCapacity = 1) as MutableSharedFlow<Any?> }
        flow.tryEmit(value)
    }

    fun clear(screen: Screen) {
        Napier.d(tag = TAG) { "clear $screen" }
        map.remove(screen)?.also {
            it.tryEmit(null)
        }
    }
}

val LocalResultStore =
    androidx.compose.runtime.staticCompositionLocalOf<ResultStore> {
        error("No RootNavigator provided")
    }

class ScreenResultEmitter(
    private val screen: Screen,
    private val resultStore: ResultStore,
) {
    fun <T> emitResult(value: T) {
        resultStore.emit(screen, value)
    }
}

val LocalScreenResultEmitter =
    androidx.compose.runtime.staticCompositionLocalOf<ScreenResultEmitter> {
        error("No RootNavigator provided")
    }

@Composable
fun rememberResultStoreNavEntryDecorator(resultStore: ResultStore = LocalResultStore.current): NavEntryDecorator<Any> =
    remember { resultStoreNavEntryDecorator(resultStore) }

fun resultStoreNavEntryDecorator(resultStore: ResultStore): NavEntryDecorator<Any> {
    val onPop: (Any) -> Unit = { contentKey ->
        Screen
            .fromJson(contentKey as String)
            .also { screen ->
                resultStore.clear(screen)
            }
    }

    return navEntryDecorator(onPop = onPop) { entry ->
        val screenResultEmitter =
            remember {
                ScreenResultEmitter(Screen.fromJson(entry.contentKey as String), resultStore)
            }
        CompositionLocalProvider(
            LocalScreenResultEmitter provides screenResultEmitter,
        ) {
            entry.Content()
        }
    }
}
