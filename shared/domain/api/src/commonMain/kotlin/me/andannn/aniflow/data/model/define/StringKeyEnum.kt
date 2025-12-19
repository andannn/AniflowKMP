/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.data.model.define

interface StringKeyEnum {
    val key: String
}

inline fun <reified T> String.deserialize(): T where T : Enum<T>, T : StringKeyEnum = enumValues<T>().first { it.key == this }
