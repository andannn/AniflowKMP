/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

const val ACTIVITY_PAGE_DATA = """
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
      "activities": [
        {
          "__typename": "TextActivity",
          "id": 934253788,
          "text": "<p>Right.  New rule whem Im picking my seasonals.  If something catches my eye but looks stupid... watch it anyway.  I always see one clip that makes me watch it and then I binge it and give it an 8.</p>",
          "userId": 185055,
          "type": "TEXT",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934253788",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 2,
          "isPinned": false,
          "createdAt": 1753770307,
          "user": {
            "id": 185055,
            "name": "TheFreakinX",
            "siteUrl": "https://anilist.co/user/185055",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b185055-vhIsaSrThcc0.jpg"
            },
            "bannerImage": "https://s4.anilist.co/file/anilistcdn/user/banner/b185055-EerXMq1hBJby.jpg",
            "options": {
              "profileColor": "gray"
            }
          }
        },      
        {
          "__typename": "ListActivity",
          "id": 934251234,
          "status": "watched episode",
          "progress": "227 - 245",
          "userId": 7279339,
          "type": "ANIME_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251234",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769825,
          "user": {
            "id": 7279339,
            "name": "tervaff",
            "siteUrl": "https://anilist.co/user/7279339",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/default.png"
            },
            "bannerImage": null,
            "options": {
              "profileColor": "red"
            }
          },
          "media": {
            "id": 269,
            "type": "ANIME",
            "format": "TV",
            "status": "FINISHED",
            "season": "FALL",
            "seasonYear": 2004,
            "startDate": {
              "year": 2004,
              "month": 10,
              "day": 5
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx269-d2GmRkJbMopq.png",
              "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx269-d2GmRkJbMopq.png",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx269-d2GmRkJbMopq.png",
              "color": "#f1a150"
            },
            "title": {
              "romaji": "BLEACH",
              "english": "Bleach",
              "native": "BLEACH"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251233,
          "status": "completed",
          "progress": null,
          "userId": 7428026,
          "type": "ANIME_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251233",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769825,
          "user": {
            "id": 7428026,
            "name": "mkvn",
            "siteUrl": "https://anilist.co/user/7428026",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b7428026-x2zIdWpA4Mrv.jpg"
            },
            "bannerImage": null,
            "options": {
              "profileColor": "pink"
            }
          },
          "media": {
            "id": 103047,
            "type": "ANIME",
            "format": "MOVIE",
            "status": "FINISHED",
            "season": "SUMMER",
            "seasonYear": 2020,
            "startDate": {
              "year": 2020,
              "month": 9,
              "day": 18
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx103047-odblDHHEdehK.jpg",
              "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx103047-odblDHHEdehK.jpg",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx103047-odblDHHEdehK.jpg",
              "color": "#1a86d6"
            },
            "title": {
              "romaji": "Violet Evergarden Movie",
              "english": "Violet Evergarden: the Movie",
              "native": "劇場版 ヴァイオレット・エヴァーガーデン"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251232,
          "status": "read chapter",
          "progress": "1 - 8",
          "userId": 7334071,
          "type": "MANGA_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251232",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769825,
          "user": {
            "id": 7334071,
            "name": "weeednimm",
            "siteUrl": "https://anilist.co/user/7334071",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/default.png"
            },
            "bannerImage": null,
            "options": {
              "profileColor": "red"
            }
          },
          "media": {
            "id": 174651,
            "type": "MANGA",
            "format": "MANGA",
            "status": "FINISHED",
            "season": null,
            "seasonYear": null,
            "startDate": {
              "year": 2023,
              "month": 9,
              "day": 10
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/large/bx174651-Ufns2Hs7VKSc.jpg",
              "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx174651-Ufns2Hs7VKSc.jpg",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/bx174651-Ufns2Hs7VKSc.jpg",
              "color": "#d61a43"
            },
            "title": {
              "romaji": "Monster Stein",
              "english": null,
              "native": "モンスターシュタイン"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251231,
          "status": "read chapter",
          "progress": "36",
          "userId": 7076645,
          "type": "MANGA_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251231",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769825,
          "user": {
            "id": 7076645,
            "name": "sodelsoda",
            "siteUrl": "https://anilist.co/user/7076645",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b7076645-3xaS6b2XmjpT.png"
            },
            "bannerImage": "https://s4.anilist.co/file/anilistcdn/user/banner/b7076645-1ptQvuBeRqZn.jpg",
            "options": {
              "profileColor": "pink"
            }
          },
          "media": {
            "id": 138928,
            "type": "MANGA",
            "format": "MANGA",
            "status": "RELEASING",
            "season": null,
            "seasonYear": null,
            "startDate": {
              "year": 2020,
              "month": 3,
              "day": 21
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/large/bx138928-uUsf7B6xMxCQ.jpg",
              "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx138928-uUsf7B6xMxCQ.jpg",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/bx138928-uUsf7B6xMxCQ.jpg",
              "color": "#e4a143"
            },
            "title": {
              "romaji": "Goukon ni Ittara Onna ga Inakatta Hanashi",
              "english": "How I Attended an All-Guy's Mixer",
              "native": "合コンに行ったら女がいなかった話"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251230,
          "status": "read chapter",
          "progress": "26",
          "userId": 7237041,
          "type": "MANGA_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251230",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769824,
          "user": {
            "id": 7237041,
            "name": "Chirag1Goyal",
            "siteUrl": "https://anilist.co/user/7237041",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/default.png"
            },
            "bannerImage": null,
            "options": {
              "profileColor": "gray"
            }
          },
          "media": {
            "id": 167229,
            "type": "MANGA",
            "format": "MANGA",
            "status": "RELEASING",
            "season": null,
            "seasonYear": null,
            "startDate": {
              "year": 2023,
              "month": 7,
              "day": 16
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/large/bx167229-RBETQJqBPODL.jpg",
              "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx167229-RBETQJqBPODL.jpg",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/bx167229-RBETQJqBPODL.jpg",
              "color": "#ffc9bb"
            },
            "title": {
              "romaji": "Eomma Meonjeo Deuseyo",
              "english": "Eat First, Mom",
              "native": "엄마 먼저 드세요"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251228,
          "status": "read chapter",
          "progress": "146 - 157",
          "userId": 6969284,
          "type": "MANGA_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251228",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769824,
          "user": {
            "id": 6969284,
            "name": "B4BoDacious",
            "siteUrl": "https://anilist.co/user/6969284",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/default.png"
            },
            "bannerImage": null,
            "options": {
              "profileColor": "gray"
            }
          },
          "media": {
            "id": 109957,
            "type": "MANGA",
            "format": "MANGA",
            "status": "RELEASING",
            "season": null,
            "seasonYear": null,
            "startDate": {
              "year": 2019,
              "month": 5,
              "day": 16
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/large/bx109957-EgJWdR7l9TBG.jpg",
              "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx109957-EgJWdR7l9TBG.jpg",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/bx109957-EgJWdR7l9TBG.jpg",
              "color": "#356be4"
            },
            "title": {
              "romaji": "Dubeon Saneun Ranker",
              "english": "Second Life Ranker",
              "native": "두번사는랭커"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251226,
          "status": "completed",
          "progress": null,
          "userId": 7421237,
          "type": "ANIME_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251226",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769823,
          "user": {
            "id": 7421237,
            "name": "aksktbk",
            "siteUrl": "https://anilist.co/user/7421237",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b7421237-WZSjBriPXF1d.jpg"
            },
            "bannerImage": null,
            "options": {
              "profileColor": "red"
            }
          },
          "media": {
            "id": 205,
            "type": "ANIME",
            "format": "TV",
            "status": "FINISHED",
            "season": "SPRING",
            "seasonYear": 2004,
            "startDate": {
              "year": 2004,
              "month": 5,
              "day": 20
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx205-7tHVFu6dPBm9.png",
              "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx205-7tHVFu6dPBm9.png",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx205-7tHVFu6dPBm9.png",
              "color": null
            },
            "title": {
              "romaji": "Samurai Champloo",
              "english": "Samurai Champloo",
              "native": "サムライチャンプルー"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251224,
          "status": "read chapter",
          "progress": "162 - 163",
          "userId": 5161204,
          "type": "MANGA_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251224",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769823,
          "user": {
            "id": 5161204,
            "name": "CntnSkyline",
            "siteUrl": "https://anilist.co/user/5161204",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b5161204-AzvAyS6qbFIA.jpg"
            },
            "bannerImage": "https://s4.anilist.co/file/anilistcdn/user/banner/b5161204-LbQxFHDBAFUq.jpg",
            "options": {
              "profileColor": "green"
            }
          },
          "media": {
            "id": 137304,
            "type": "MANGA",
            "format": "MANGA",
            "status": "RELEASING",
            "season": null,
            "seasonYear": null,
            "startDate": {
              "year": 2021,
              "month": 7,
              "day": 22
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/large/bx137304-XZ91kOmUIMQo.png",
              "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx137304-XZ91kOmUIMQo.png",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/bx137304-XZ91kOmUIMQo.png",
              "color": "#e4935d"
            },
            "title": {
              "romaji": "Gwangmahoegwi",
              "english": "Return of the Mad Demon",
              "native": "광마회귀"
            }
          }
        },
        {
          "__typename": "ListActivity",
          "id": 934251223,
          "status": "read chapter",
          "progress": "12",
          "userId": 5844068,
          "type": "MANGA_LIST",
          "replyCount": 0,
          "siteUrl": "https://anilist.co/activity/934251223",
          "isLocked": false,
          "isLiked": false,
          "likeCount": 0,
          "isPinned": false,
          "createdAt": 1753769823,
          "user": {
            "id": 5844068,
            "name": "SwankaGar",
            "siteUrl": "https://anilist.co/user/5844068",
            "avatar": {
              "large": "https://s4.anilist.co/file/anilistcdn/user/avatar/large/b5844068-AJdzTBzC39B3.jpg"
            },
            "bannerImage": "https://s4.anilist.co/file/anilistcdn/user/banner/b5844068-71s16tVsnkQE.jpg",
            "options": {
              "profileColor": "gray"
            }
          },
          "media": {
            "id": 194523,
            "type": "MANGA",
            "format": "MANGA",
            "status": "RELEASING",
            "season": null,
            "seasonYear": null,
            "startDate": {
              "year": 2025,
              "month": 6,
              "day": 14
            },
            "coverImage": {
              "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/large/bx194523-cLAK6gTAKNkM.jpg",
              "large": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/medium/bx194523-cLAK6gTAKNkM.jpg",
              "medium": "https://s4.anilist.co/file/anilistcdn/media/manga/cover/small/bx194523-cLAK6gTAKNkM.jpg",
              "color": "#50a1e4"
            },
            "title": {
              "romaji": "Gwonwanghwansaeng",
              "english": null,
              "native": "권왕환생"
            }
          }
        }
      ]
    }
  }
}
"""
