/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

interface StringKeyEnum {
    val key: String

    companion object {
        inline fun <reified T> deserialize(key: String): T where T : Enum<T>, T : StringKeyEnum = enumValues<T>().first { it.key == key }
    }
}
