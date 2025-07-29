/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

const val SEARCH_MEDIA_RESULT_PAGE_DATA = """
{
  "data": {
    "Page": {
      "pageInfo": {
        "total": 5000,
        "perPage": 10,
        "currentPage": 1,
        "lastPage": 500,
        "hasNextPage": true
      },
      "media": [
        {
          "id": 1,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SPRING",
          "seasonYear": 1998,
          "startDate": {
            "year": 1998,
            "month": 4,
            "day": 3
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx1-GCsPm7waJ4kS.png",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx1-GCsPm7waJ4kS.png",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx1-GCsPm7waJ4kS.png",
            "color": "#f16b50"
          },
          "title": {
            "romaji": "Cowboy Bebop",
            "english": "Cowboy Bebop",
            "native": "カウボーイビバップ"
          }
        },
        {
          "id": 5,
          "type": "ANIME",
          "format": "MOVIE",
          "status": "FINISHED",
          "season": "SUMMER",
          "seasonYear": 2001,
          "startDate": {
            "year": 2001,
            "month": 9,
            "day": 1
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx5-NozHwXWdNLCz.jpg",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx5-NozHwXWdNLCz.jpg",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx5-NozHwXWdNLCz.jpg",
            "color": "#f13500"
          },
          "title": {
            "romaji": "Cowboy Bebop: Tengoku no Tobira",
            "english": "Cowboy Bebop: The Movie - Knockin' on Heaven's Door",
            "native": "カウボーイビバップ天国の扉"
          }
        },
        {
          "id": 6,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SPRING",
          "seasonYear": 1998,
          "startDate": {
            "year": 1998,
            "month": 4,
            "day": 1
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx6-wd4saT1JzStH.jpg",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx6-wd4saT1JzStH.jpg",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx6-wd4saT1JzStH.jpg",
            "color": "#356bae"
          },
          "title": {
            "romaji": "TRIGUN",
            "english": "Trigun",
            "native": "TRIGUN"
          }
        },
        {
          "id": 7,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SUMMER",
          "seasonYear": 2002,
          "startDate": {
            "year": 2002,
            "month": 7,
            "day": 2
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx7-6uh1fPvbgS9t.png",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx7-6uh1fPvbgS9t.png",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx7-6uh1fPvbgS9t.png",
            "color": "#e4935d"
          },
          "title": {
            "romaji": "Witch Hunter ROBIN",
            "english": "Witch Hunter ROBIN",
            "native": "Witch Hunter ROBIN"
          }
        },
        {
          "id": 8,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "FALL",
          "seasonYear": 2004,
          "startDate": {
            "year": 2004,
            "month": 9,
            "day": 30
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/b8-ReS3TwSgrDDi.jpg",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/b8-ReS3TwSgrDDi.jpg",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/b8-ReS3TwSgrDDi.jpg",
            "color": "#e46b50"
          },
          "title": {
            "romaji": "Bouken Ou Beet",
            "english": "Beet the Vandel Buster",
            "native": "冒険王ビィト"
          }
        },
        {
          "id": 15,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SPRING",
          "seasonYear": 2005,
          "startDate": {
            "year": 2005,
            "month": 4,
            "day": 6
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx15-A4F2t0TgWoi4.png",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx15-A4F2t0TgWoi4.png",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx15-A4F2t0TgWoi4.png",
            "color": "#d6bb1a"
          },
          "title": {
            "romaji": "Eyeshield 21",
            "english": "Eyeshield 21",
            "native": "アイシールド21"
          }
        },
        {
          "id": 16,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SPRING",
          "seasonYear": 2005,
          "startDate": {
            "year": 2005,
            "month": 4,
            "day": 15
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx16-S9k8qahNXoYP.jpg",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx16-S9k8qahNXoYP.jpg",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx16-S9k8qahNXoYP.jpg",
            "color": "#aee486"
          },
          "title": {
            "romaji": "Hachimitsu to Clover",
            "english": "Honey and Clover",
            "native": "ハチミツとクローバー"
          }
        },
        {
          "id": 17,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "FALL",
          "seasonYear": 2002,
          "startDate": {
            "year": 2002,
            "month": 9,
            "day": 11
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx17-6kqIbdUk3dgi.png",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx17-6kqIbdUk3dgi.png",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx17-6kqIbdUk3dgi.png",
            "color": "#43aee4"
          },
          "title": {
            "romaji": "Hungry Heart: Wild Striker",
            "english": null,
            "native": "ハングリーハート Wild Striker"
          }
        },
        {
          "id": 18,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SPRING",
          "seasonYear": 2004,
          "startDate": {
            "year": 2004,
            "month": 4,
            "day": 17
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/b18-r7IirVmwP89u.jpg",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/b18-r7IirVmwP89u.jpg",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/b18-r7IirVmwP89u.jpg",
            "color": "#e46b50"
          },
          "title": {
            "romaji": "Initial D FOURTH STAGE",
            "english": "Initial D 4th Stage",
            "native": "頭文字〈イニシャル〉D FOURTH STAGE"
          }
        },
        {
          "id": 19,
          "type": "ANIME",
          "format": "TV",
          "status": "FINISHED",
          "season": "SPRING",
          "seasonYear": 2004,
          "startDate": {
            "year": 2004,
            "month": 4,
            "day": 7
          },
          "coverImage": {
            "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx19-gtMC64182sm4.jpg",
            "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx19-gtMC64182sm4.jpg",
            "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx19-gtMC64182sm4.jpg",
            "color": null
          },
          "title": {
            "romaji": "MONSTER",
            "english": "Monster",
            "native": "MONSTER"
          }
        }
      ]
    }
  }
}
"""
