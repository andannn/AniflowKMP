package me.andannn.aniflow.data

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * Logger utility to enable debug logging using Napier.
 */
object Logger {
    fun enableDebugLog() {
        Napier.base(DebugAntilog())
    }
}
