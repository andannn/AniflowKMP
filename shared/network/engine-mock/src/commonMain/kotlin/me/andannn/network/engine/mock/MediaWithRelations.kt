/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

const val MEDIA_WITH_RELATIONS = """
    {
  "data": {
    "Media": {
      "id": 1,
      "title": {
        "romaji": "Cowboy Bebop",
        "english": "Cowboy Bebop",
        "native": "カウボーイビバップ"
      },
      "type": "ANIME",
      "description": "Enter a world in the distant future, where Bounty Hunters roam the solar system. Spike and Jet, bounty hunting partners, set out on journeys in an ever struggling effort to win bounty rewards to survive.<br><br>\nWhile traveling, they meet up with other very interesting people. Could Faye, the beautiful and ridiculously poor gambler, Edward, the computer genius, and Ein, the engineered dog be a good addition to the group?",
      "episodes": 26,
      "seasonYear": 1998,
      "season": "SPRING",
      "source": "ORIGINAL",
      "genres": [
        "Action",
        "Adventure",
        "Drama",
        "Sci-Fi"
      ],
      "status": "FINISHED",
      "hashtag": null,
      "isFavourite": false,
      "externalLinks": [
        {
          "id": 609,
          "url": "http://www.hulu.com/cowboy-bebop",
          "site": "Hulu",
          "type": "STREAMING",
          "siteId": 7,
          "color": "#1CE783",
          "icon": "https://s4.anilist.co/file/anilistcdn/link/icon/7-rM06PQyWONGC.png"
        },
        {
          "id": 3346,
          "url": "http://www.crunchyroll.com/cowboy-bebop",
          "site": "Crunchyroll",
          "type": "STREAMING",
          "siteId": 5,
          "color": "#F88B24",
          "icon": "https://s4.anilist.co/file/anilistcdn/link/icon/5-AWN2pVlluCOO.png"
        },
        {
          "id": 13042,
          "url": "https://www.amazon.com/gp/video/detail/B00R2KO8ZE/",
          "site": "Amazon Prime Video",
          "type": "STREAMING",
          "siteId": 21,
          "color": "#FF9900",
          "icon": "https://s4.anilist.co/file/anilistcdn/link/icon/21-bDoNIomehkOx.png"
        },
        {
          "id": 16098,
          "url": "https://tubitv.com/series/2052",
          "site": "Tubi TV",
          "type": "STREAMING",
          "siteId": 30,
          "color": "#7408FF",
          "icon": "https://s4.anilist.co/file/anilistcdn/link/icon/30-RoKDJtmAVpZK.png"
        },
        {
          "id": 16099,
          "url": "https://www.adultswim.com/videos/cowboy-bebop",
          "site": "Adult Swim",
          "type": "STREAMING",
          "siteId": 28,
          "color": "#000000",
          "icon": "https://s4.anilist.co/file/anilistcdn/link/icon/28-W1L8AHW0O4xE.png"
        },
        {
          "id": 39888,
          "url": "https://www.netflix.com/title/80001305",
          "site": "Netflix",
          "type": "STREAMING",
          "siteId": 10,
          "color": "#E50914",
          "icon": "https://s4.anilist.co/file/anilistcdn/link/icon/10-rVGPom8RCiwH.png"
        },
        {
          "id": 83633,
          "url": "https://cowboy-bebop.net/",
          "site": "Official Site",
          "type": "INFO",
          "siteId": 1,
          "color": null,
          "icon": null
        }
      ],
      "rankings": [
        {
          "rank": 48,
          "type": "RATED",
          "allTime": true
        },
        {
          "rank": 54,
          "type": "POPULAR",
          "allTime": true
        },
        {
          "rank": 1,
          "type": "RATED",
          "allTime": false
        },
        {
          "rank": 1,
          "type": "POPULAR",
          "allTime": false
        }
      ],
      "trailer": {
        "id": "OhNwckCLzis",
        "site": "youtube",
        "thumbnail": "https://i.ytimg.com/vi/OhNwckCLzis/hqdefault.jpg"
      },
      "coverImage": {
        "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx1-GCsPm7waJ4kS.png",
        "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx1-GCsPm7waJ4kS.png",
        "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx1-GCsPm7waJ4kS.png",
        "color": "#f16b50"
      },
      "format": "TV",
      "bannerImage": "https://s4.anilist.co/file/anilistcdn/media/anime/banner/1-OquNCNB6srGe.jpg",
      "averageScore": 86,
      "meanScore": 86,
      "favourites": 26083,
      "trending": 6,
      "nextAiringEpisode": null,
      "relations": {
        "edges": [
          {
            "relationType": "SIDE_STORY",
            "node": {
              "id": 5,
              "title": {
                "romaji": "Cowboy Bebop: Tengoku no Tobira",
                "english": "Cowboy Bebop: The Movie - Knockin' on Heaven's Door",
                "native": "カウボーイビバップ天国の扉"
              },
              "type": "ANIME",
              "format": "MOVIE",
              "status": "FINISHED",
              "coverImage": {
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx5-NozHwXWdNLCz.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx5-NozHwXWdNLCz.jpg",
                "color": "#f13500"
              },
              "bannerImage": "https://s4.anilist.co/file/anilistcdn/media/anime/banner/5-VOcSZFepDDhm.jpg",
              "isFavourite": false,
              "averageScore": 82,
              "meanScore": 82,
              "favourites": 1433,
              "trending": 1,
              "nextAiringEpisode": null
            }
          },
          {
            "relationType": "SIDE_STORY",
            "node": {
              "id": 17205,
              "title": {
                "romaji": "Cowboy Bebop: Ein no Natsuyasumi",
                "english": "Ein's Summer Vacation",
                "native": "アインのなつやすみ"
              },
              "type": "ANIME",
              "format": "SPECIAL",
              "status": "FINISHED",
              "coverImage": {
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx17205-Dk5nmaKD7hPM.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx17205-Dk5nmaKD7hPM.jpg",
                "color": "#35f1a1"
              },
              "bannerImage": "https://s4.anilist.co/file/anilistcdn/media/anime/banner/17205-V924IzRELNKt.jpg",
              "isFavourite": false,
              "averageScore": 60,
              "meanScore": 61,
              "favourites": 37,
              "trending": 0,
              "nextAiringEpisode": null
            }
          },
          {
            "relationType": "ADAPTATION",
            "node": {
              "id": 30173,
              "title": {
                "romaji": "Cowboy Bebop",
                "english": "Cowboy Bebop",
                "native": "カウボーイビバップ"
              },
              "type": "MANGA",
              "format": "MANGA",
              "status": "FINISHED",
              "coverImage": {
                "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/nx30173-SU7cu3H9jLXT.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/nx30173-SU7cu3H9jLXT.jpg",
                "color": "#fec950"
              },
              "bannerImage": null,
              "isFavourite": false,
              "averageScore": 66,
              "meanScore": 67,
              "favourites": 98,
              "trending": 0,
              "nextAiringEpisode": null
            }
          },
          {
            "relationType": "ADAPTATION",
            "node": {
              "id": 30174,
              "title": {
                "romaji": "Shooting Star Bebop: Cowboy Bebop",
                "english": "Cowboy Bebop Shooting Star",
                "native": "シューティングスタービバップ カウボーイビバップ"
              },
              "type": "MANGA",
              "format": "MANGA",
              "status": "FINISHED",
              "coverImage": {
                "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/b30174-RTbUXsQNPNwd.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/b30174-RTbUXsQNPNwd.jpg",
                "color": "#e4a11a"
              },
              "bannerImage": null,
              "isFavourite": false,
              "averageScore": 58,
              "meanScore": 61,
              "favourites": 12,
              "trending": 0,
              "nextAiringEpisode": null
            }
          },
          {
            "relationType": "SUMMARY",
            "node": {
              "id": 4037,
              "title": {
                "romaji": "Cowboy Bebop: Yoseatsume Blues",
                "english": null,
                "native": "カウボーイビバップ よせあつめブルース"
              },
              "type": "ANIME",
              "format": "SPECIAL",
              "status": "FINISHED",
              "coverImage": {
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx4037-zpDbjycoZhNI.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx4037-zpDbjycoZhNI.png",
                "color": "#e4d65d"
              },
              "bannerImage": "https://s4.anilist.co/file/anilistcdn/media/anime/banner/n4037-1rwmIePHBzB7.jpg",
              "isFavourite": false,
              "averageScore": 70,
              "meanScore": 71,
              "favourites": 97,
              "trending": 0,
              "nextAiringEpisode": null
            }
          }
        ]
      }
    }
  }
}
"""
