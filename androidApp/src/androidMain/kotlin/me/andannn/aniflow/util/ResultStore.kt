package me.andannn.aniflow.util

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.navEntryDecorator
import io.github.aakira.napier.Napier
import kotlinx.coroutines.suspendCancellableCoroutine
import me.andannn.aniflow.ui.Screen
import kotlin.coroutines.Continuation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KType
import kotlin.reflect.typeOf

const val TAG = "ResultStore"

class ResultStore {
    @VisibleForTesting
    val continuationList = mutableListOf<ResultContinuation>()

    suspend inline fun <reified T : Any> awaitResultOf(screen: Screen): T =
        suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation {
                continuationList.removeIf { it.continuation == cont }
            }

            val continuationOrNull =
                continuationList.firstOrNull { continuation -> continuation.screen == screen }
            if (continuationOrNull != null) {
                throw IllegalStateException("Screen $screen has already been requested")
            }

            continuationList.add(
                ResultContinuation(
                    typeInfo = typeOf<T>(),
                    screen = screen,
                    continuation = cont as Continuation<Any>,
                ),
            )
        }

    inline fun <reified T : Any> emit(
        screen: Screen,
        value: T,
    ) {
        Napier.d(tag = TAG) { "emit $screen value $value" }
        val cont =
            continuationList
                .firstOrNull { it.screen == screen }

        if (cont == null) {
            return
        }

        if (cont.typeInfo != typeOf<T>()) {
            cancel(screen)
            throw IllegalArgumentException("Expected ${cont.typeInfo}, got ${typeOf<T>()}")
        }

        continuationList.remove(cont)
        cont.continuation.resume(value)
    }

    fun cancel(screen: Screen) {
        val cont =
            continuationList
                .firstOrNull { it.screen == screen }

        if (cont == null) {
            return
        }

        cont.continuation.resumeWithException(CancellationException("Cancelled by user"))
        continuationList.remove(cont)
    }

    class ResultContinuation(
        val typeInfo: KType,
        val screen: Screen,
        val continuation: Continuation<Any>,
    )
}

val LocalResultStore =
    androidx.compose.runtime.staticCompositionLocalOf<ResultStore> {
        error("No RootNavigator provided")
    }

class ScreenResultEmitter(
    val screen: Screen,
    val resultStore: ResultStore,
) {
    inline fun <reified T : Any> emitResult(value: T) {
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
        val screen = Screen.fromJson(contentKey as String)
        resultStore.cancel(screen)
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
