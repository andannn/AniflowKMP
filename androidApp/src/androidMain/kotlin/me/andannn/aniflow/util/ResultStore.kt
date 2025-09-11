package me.andannn.aniflow.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.SavedStateNavEntryDecorator
import androidx.navigation3.runtime.navEntryDecorator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import me.andannn.aniflow.ui.RootNavigator
import me.andannn.aniflow.ui.Screen

class ResultStore {
    private val map = mutableMapOf<Screen, MutableSharedFlow<Any?>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> resultsOf(screen: Screen): SharedFlow<T?> {
        val flow = map.getOrPut(screen) { MutableSharedFlow(extraBufferCapacity = 1) }
        return flow as SharedFlow<T>
    }

    fun <T> emit(
        screen: Screen,
        value: T,
    ) {
        val flow = map.getOrPut(screen) { MutableSharedFlow(extraBufferCapacity = 1) }
        flow.tryEmit(value)
    }

    fun clear(screen: Screen) {
        map.remove(screen)?.also {
            it.tryEmit(null)
        }
    }
}

val LocalResultStore =
    androidx.compose.runtime.staticCompositionLocalOf<ResultStore> {
        error("No RootNavigator provided")
    }

@Composable
fun rememberResultStoreNavEntryDecorator(resultStore: ResultStore = LocalResultStore.current): NavEntryDecorator<Any> =
    remember { resultStoreNavEntryDecorator(resultStore) }

fun resultStoreNavEntryDecorator(resultStore: ResultStore): NavEntryDecorator<Any> {
    val onPop: (Any) -> Unit = { contentKey ->
    }

    return navEntryDecorator(onPop = onPop) { entry ->
        Log.d("JQN", "resultStoreNavEntryDecorator: ${entry.contentKey}")
        Log.d("JQN", "resultStoreNavEntryDecorator: ${Screen.fromJson(entry.contentKey as String)}")
        entry.Content()
    }
}
