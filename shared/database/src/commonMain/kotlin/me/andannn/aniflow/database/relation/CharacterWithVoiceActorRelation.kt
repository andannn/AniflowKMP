/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.aniflow.database.relation

import me.andannn.aniflow.database.schema.CharacterEntity
import me.andannn.aniflow.database.schema.StaffEntity

data class CharacterWithVoiceActorRelation(
    val character: CharacterEntity,
    val voiceActor: StaffEntity?,
    val role: String?,
) {
    companion object {
        fun mapTo(
            id: String,
            largeImage: String?,
            mediumImage: String?,
            firstName: String?,
            middleName: String?,
            lastName: String?,
            fullName: String?,
            nativeName: String?,
            description: String?,
            gender: String?,
            age: String?,
            bloodType: String?,
            siteUrl: String?,
            dateOfBirth: String?,
            favourites: Long?,
            isFavourite: Boolean?,
            role: String?,
            staffId: String?,
            staffLargeImage: String?,
            staffMediumImage: String?,
            staffFirstName: String?,
            staffMiddleName: String?,
            staffLastName: String?,
            staffFullName: String?,
            staffNativeName: String?,
            staffDescription: String?,
            staffGender: String?,
            staffSiteUrl: String?,
            staffDateOfBirth: String?,
            staffDateOfDeath: String?,
            staffAge: Long?,
            staffIsFavourite: Boolean?,
            staffYearsActive: String?,
            staffHomeTown: String?,
            staffBloodType: String?,
        ): CharacterWithVoiceActorRelation =
            CharacterWithVoiceActorRelation(
                role = role,
                character =
                    CharacterEntity(
                        id = id,
                        largeImage = largeImage,
                        mediumImage = mediumImage,
                        firstName = firstName,
                        middleName = middleName,
                        lastName = lastName,
                        fullName = fullName,
                        nativeName = nativeName,
                        description = description,
                        gender = gender,
                        age = age,
                        bloodType = bloodType,
                        siteUrl = siteUrl,
                        dateOfBirth = dateOfBirth,
                        favourites = favourites,
                        isFavourite = isFavourite,
                    ),
                voiceActor =
                    if (staffId == null) {
                        null
                    } else {
                        StaffEntity(
                            id = staffId,
                            largeImage = staffLargeImage,
                            mediumImage = staffMediumImage,
                            firstName = staffFirstName,
                            middleName = staffMiddleName,
                            lastName = staffLastName,
                            fullName = staffFullName,
                            nativeName = staffNativeName,
                            description = staffDescription,
                            gender = staffGender,
                            siteUrl = staffSiteUrl,
                            dateOfBirth = staffDateOfBirth,
                            dateOfDeath = staffDateOfDeath,
                            age = staffAge,
                            isFavourite = staffIsFavourite,
                            yearsActive = staffYearsActive,
                            homeTown = staffHomeTown,
                            bloodType = staffBloodType,
                        )
                    },
            )
    }
}
