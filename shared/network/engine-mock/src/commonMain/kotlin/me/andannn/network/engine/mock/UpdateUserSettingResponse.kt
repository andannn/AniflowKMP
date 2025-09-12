/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

val UPDATE_USER_SETTING_RESPONSE =
    """
    {
      "data" : {
        "UpdateUser" : {
          "id" : 6378393,
          "name" : "andannn",
          "avatar" : {
            "large" : "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b6378393-3BsPEhU6Tz5l.png",
            "medium" : "https://s4.anilist.co/file/anilistcdn/user/avatar/medium/b6378393-3BsPEhU6Tz5l.png"
          },
          "bannerImage" : "https://s4.anilist.co/file/anilistcdn/user/banner/b6378393-qmyrEYbiA2HR.jpg",
          "unreadNotificationCount" : 0,
          "options" : {
            "titleLanguage" : "ROMAJI",
            "displayAdultContent" : false,
            "airingNotifications" : true,
            "profileColor" : "blue",
            "timezone" : null,
            "activityMergeTime" : 720,
            "staffNameLanguage" : "NATIVE",
            "restrictMessagesToFollowing" : false
          },
          "mediaListOptions" : {
            "scoreFormat" : "POINT_5"
          }
        }
      }
    }
    """.trimIndent()
