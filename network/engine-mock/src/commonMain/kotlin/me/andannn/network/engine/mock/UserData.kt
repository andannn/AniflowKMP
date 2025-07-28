package me.andannn.network.engine.mock

internal val USER_DATA =
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
          "unreadNotificationCount" : 13,
          "options" : {
            "titleLanguage" : "ENGLISH",
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
