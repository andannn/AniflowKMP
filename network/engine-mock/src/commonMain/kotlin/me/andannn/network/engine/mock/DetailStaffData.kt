/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

internal const val DETAIL_STUDIO_DATA = """
{
  "data": {
    "Studio": {
      "id": 322,
      "name": "Bee Media",
      "isAnimationStudio": true,
      "siteUrl": "https://anilist.co/studio/322",
      "isFavourite": false,
      "media": {
        "pageInfo": {
          "total": 12,
          "perPage": 25,
          "currentPage": 1,
          "lastPage": 1,
          "hasNextPage": false
        },
        "edges": [
          {
            "relationType": null,
            "node": {
              "id": 125640,
              "type": "ANIME",
              "format": "TV",
              "status": "FINISHED",
              "episodes": 13,
              "seasonYear": 2021,
              "season": "SUMMER",
              "source": "MANGA",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx125640-RghOmQmgKzWi.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx125640-RghOmQmgKzWi.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx125640-RghOmQmgKzWi.jpg",
                "color": "#e4ae50"
              },
              "title": {
                "romaji": "Getter Robo Arc",
                "english": null,
                "native": "ゲッターロボ アーク"
              },
              "startDate": {
                "year": 2021,
                "month": 7,
                "day": 4
              },
              "endDate": {
                "year": 2021,
                "month": 9,
                "day": 26
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 21231,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 3,
              "seasonYear": 2015,
              "season": "FALL",
              "source": "OTHER",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/21231-gkpwtjqJGxTo.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/21231-gkpwtjqJGxTo.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/21231-gkpwtjqJGxTo.jpg",
                "color": "#e4ae5d"
              },
              "title": {
                "romaji": "Cyborg 009 VS Devilman",
                "english": "Cyborg 009 vs Devilman",
                "native": "サイボーグ009VSデビルマン"
              },
              "startDate": {
                "year": 2015,
                "month": 10,
                "day": 17
              },
              "endDate": {
                "year": 2015,
                "month": 10,
                "day": 17
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 5485,
              "type": "ANIME",
              "format": "TV",
              "status": "FINISHED",
              "episodes": 26,
              "seasonYear": 2009,
              "season": "SPRING",
              "source": "MANGA",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx5485-q6IvWwQZchGf.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx5485-q6IvWwQZchGf.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx5485-q6IvWwQZchGf.jpg",
                "color": "#f15d35"
              },
              "title": {
                "romaji": "Shin Mazinger Shougeki! Z-hen",
                "english": "Mazinger Edition Z: The Impact!",
                "native": "真マジンガー 衝撃！Z編"
              },
              "startDate": {
                "year": 2009,
                "month": 4,
                "day": 4
              },
              "endDate": {
                "year": 2009,
                "month": 9,
                "day": 26
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 1243,
              "type": "ANIME",
              "format": "TV",
              "status": "FINISHED",
              "episodes": 24,
              "seasonYear": 2006,
              "season": "SUMMER",
              "source": "OTHER",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/b1243-KqREWydioYdx.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/b1243-KqREWydioYdx.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/b1243-KqREWydioYdx.jpg",
                "color": null
              },
              "title": {
                "romaji": "NIGHT HEAD GENESIS",
                "english": null,
                "native": "NIGHT HEAD GENESIS"
              },
              "startDate": {
                "year": 2006,
                "month": 6,
                "day": 17
              },
              "endDate": {
                "year": 2006,
                "month": 12,
                "day": 31
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 2234,
              "type": "ANIME",
              "format": "TV_SHORT",
              "status": "FINISHED",
              "episodes": 30,
              "seasonYear": 2004,
              "season": "SPRING",
              "source": null,
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/2234.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/2234.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/2234.jpg",
                "color": "#e4a11a"
              },
              "title": {
                "romaji": "Panda-Z: THE ROBONIMATION",
                "english": null,
                "native": "パンダーゼット THE ROBONIMATION"
              },
              "startDate": {
                "year": 2004,
                "month": 4,
                "day": 12
              },
              "endDate": {
                "year": 2004,
                "month": 10,
                "day": 4
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 2978,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 13,
              "seasonYear": 2004,
              "season": "SPRING",
              "source": "ORIGINAL",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx2978-wybJCsj1WaIh.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx2978-wybJCsj1WaIh.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx2978-wybJCsj1WaIh.jpg",
                "color": "#43aeff"
              },
              "title": {
                "romaji": "Shin Getter Robo",
                "english": "New Getter Robo",
                "native": "新ゲッターロボ"
              },
              "startDate": {
                "year": 2004,
                "month": 4,
                "day": 9
              },
              "endDate": {
                "year": 2004,
                "month": 9,
                "day": 10
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 2734,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 1,
              "seasonYear": 2003,
              "season": "SUMMER",
              "source": "MANGA",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/2734.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/2734.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/2734.jpg",
                "color": "#d61a1a"
              },
              "title": {
                "romaji": "Mazinkaiser: Shitou! Ankoku Dai Shogun",
                "english": "Mazinkaiser vs. Great General of Darkness",
                "native": "マジンカイザー　死闘！暗黒大将軍"
              },
              "startDate": {
                "year": 2003,
                "month": 7,
                "day": 25
              },
              "endDate": {
                "year": 2003,
                "month": 7,
                "day": 25
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 1064,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 7,
              "seasonYear": 2001,
              "season": "FALL",
              "source": "MANGA",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx1064-LZyEvupm6y8G.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx1064-LZyEvupm6y8G.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx1064-LZyEvupm6y8G.jpg",
                "color": "#e46b43"
              },
              "title": {
                "romaji": "Mazinkaiser",
                "english": "Mazinkaiser",
                "native": "マジンカイザー"
              },
              "startDate": {
                "year": 2001,
                "month": 9,
                "day": 25
              },
              "endDate": {
                "year": 2002,
                "month": 9,
                "day": 20
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 12885,
              "type": "ANIME",
              "format": "SPECIAL",
              "status": "FINISHED",
              "episodes": 3,
              "seasonYear": 2000,
              "season": "FALL",
              "source": null,
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/12885.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/12885.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/12885.jpg",
                "color": "#e4931a"
              },
              "title": {
                "romaji": "Dynamic Super Robots Grand Battle",
                "english": null,
                "native": "ダイナミックスーパーロボット総進撃!!"
              },
              "startDate": {
                "year": 2000,
                "month": 12,
                "day": 21
              },
              "endDate": {
                "year": 2001,
                "month": 6,
                "day": 25
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 938,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 4,
              "seasonYear": 2000,
              "season": "FALL",
              "source": null,
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx938-VZBqo0JtsljD.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx938-VZBqo0JtsljD.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/bx938-VZBqo0JtsljD.jpg",
                "color": "#d61a28"
              },
              "title": {
                "romaji": "Shin Getter Robo vs. Neo Getter Robo",
                "english": "Shin Getter Robo vs. Neo Getter Robo",
                "native": "真ゲッターロボ対ネオゲッターロボ"
              },
              "startDate": {
                "year": 2000,
                "month": 12,
                "day": 21
              },
              "endDate": {
                "year": 2001,
                "month": 6,
                "day": 25
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 2218,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 1,
              "seasonYear": null,
              "season": null,
              "source": "LIGHT_NOVEL",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/2218.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/2218.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/2218.jpg",
                "color": "#e4e443"
              },
              "title": {
                "romaji": "Fujimi 2-choume Koukyougakudan: Kanrei Zensen/Ame nochi Arashi",
                "english": null,
                "native": "富士見二丁目交響楽団 寒冷前線/雨のち嵐"
              },
              "startDate": {
                "year": 1997,
                "month": 7,
                "day": 22
              },
              "endDate": {
                "year": 1997,
                "month": 7,
                "day": 22
              }
            }
          },
          {
            "relationType": null,
            "node": {
              "id": 1059,
              "type": "ANIME",
              "format": "OVA",
              "status": "FINISHED",
              "episodes": 2,
              "seasonYear": 1996,
              "season": "SPRING",
              "source": "OTHER",
              "coverImage": {
                "extraLarge": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/1059.jpg",
                "large": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/1059.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/small/1059.jpg",
                "color": "#e4ae5d"
              },
              "title": {
                "romaji": "Mutant Turtles: Choujin Densetsu-hen",
                "english": null,
                "native": "ミュータント タートルズ 超人伝説編"
              },
              "startDate": {
                "year": 1996,
                "month": 5,
                "day": 21
              },
              "endDate": {
                "year": 1996,
                "month": 5,
                "day": 21
              }
            }
          }
        ]
      }
    }
  }
}
"""
