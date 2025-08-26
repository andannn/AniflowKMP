package me.andannn.aniflow.data.model.relation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class MediaWithMediaListItemTest {
    @OptIn(ExperimentalTime::class)
    @Test
    fun isElapsedTest() {
        val time =
            Clock.System
                .now()
                .minus((24).hours)
        assertTrue {
            isElapsed(
                1,
                time.minus(10.milliseconds),
            )
        }

        assertFalse {
            isElapsed(
                1,
                time.plus(10.milliseconds),
            )
        }
    }
}
