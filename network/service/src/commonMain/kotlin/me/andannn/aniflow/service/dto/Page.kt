/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.service.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject

@Serializable
data class PageWrapper<T>(
    @SerialName("Page")
    val page: Page<T>,
)

@Serializable(with = PageSerializer::class)
data class Page<T>(
    val pageInfo: PageInfo? = null,
    val items: List<T>,
)

internal class PageSerializer : KSerializer<Page<*>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Page") {
            element<PageInfo>("pageInfo", isOptional = true)
        }

    override fun deserialize(decoder: Decoder): Page<*> {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("This class can only be loaded using JSON")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject
        val pageInfo =
            jsonObject["pageInfo"]?.let {
                jsonDecoder.json.decodeFromJsonElement(PageInfo.serializer(), it)
            }
        val itemsEntry =
            jsonObject.entries.firstOrNull { it.key != "pageInfo" }
                ?: throw SerializationException("Missing items field")
        val itemSerializer =
            ITEM_SERIALIZER_MAP[itemsEntry.key]
                ?: throw SerializationException("No serializer found for items of type: ${itemsEntry.key}")
        return Page(
            pageInfo = pageInfo,
            items =
                jsonDecoder.json.decodeFromJsonElement(
                    deserializer = ListSerializer(itemSerializer),
                    element = itemsEntry.value,
                ),
        )
    }

    override fun serialize(
        encoder: Encoder,
        value: Page<*>,
    ): Unit = throw NotImplementedError("Serialization not implemented")

    companion object {
        private val ITEM_SERIALIZER_MAP =
            mapOf<String, KSerializer<*>>(
                "media" to Media.serializer(),
                "mediaList" to MediaList.serializer(),
                "airingSchedules" to AiringSchedule.serializer(),
            )
    }
}
