/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

const val CHARACTER_PAGE_DATA = """
{
  "data": {
    "Media": {
      "characters": {
        "pageInfo": {
          "total": 500,
          "perPage": 10,
          "currentPage": 1,
          "lastPage": 50,
          "hasNextPage": true
        },
        "edges": [
          {
            "role": "MAIN",
            "node": {
              "id": 1,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b1-ChxaldmieFlQ.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b1-ChxaldmieFlQ.png"
              },
              "name": {
                "first": "Spike",
                "middle": null,
                "last": "Spiegel",
                "full": "Spike Spiegel",
                "native": "スパイク・スピーゲル"
              }
            },
            "voiceActors": [
              {
                "id": 95011,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95011-2RfLzncNyvbR.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95011-2RfLzncNyvbR.png"
                },
                "name": {
                  "first": "Kouichi",
                  "middle": null,
                  "last": "Yamadera",
                  "full": "Kouichi Yamadera",
                  "native": "山寺宏一"
                }
              },
              {
                "id": 95012,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95012-jnlK6VyCTf9P.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95012-jnlK6VyCTf9P.png"
                },
                "name": {
                  "first": "Steven",
                  "middle": "",
                  "last": "Blum",
                  "full": "Steven Blum",
                  "native": null
                }
              },
              {
                "id": 109781,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/14781.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/14781.jpg"
                },
                "name": {
                  "first": "Ja hyeong",
                  "middle": null,
                  "last": "Gu",
                  "full": "Ja hyeong Gu",
                  "native": "자형 구"
                }
              },
              {
                "id": 95781,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/781.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/781.jpg"
                },
                "name": {
                  "first": "Massimo",
                  "middle": null,
                  "last": "De Ambrosis",
                  "full": "Massimo De Ambrosis",
                  "native": null
                }
              },
              {
                "id": 105028,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/10028.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/10028.jpg"
                },
                "name": {
                  "first": "Yamil",
                  "middle": null,
                  "last": "Atala",
                  "full": "Yamil Atala",
                  "native": null
                }
              },
              {
                "id": 96099,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n96099-n1wxTRslU9gj.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n96099-n1wxTRslU9gj.jpg"
                },
                "name": {
                  "first": "Guilherme",
                  "middle": null,
                  "last": "Briggs",
                  "full": "Guilherme Briggs",
                  "native": "Guilherme Neves Briggs"
                }
              },
              {
                "id": 158227,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n158227-ADa1018DQxtc.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n158227-ADa1018DQxtc.png"
                },
                "name": {
                  "first": "Bruno",
                  "middle": null,
                  "last": "Mullenaerts",
                  "full": "Bruno Mullenaerts",
                  "native": null
                }
              },
              {
                "id": 145190,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n145190-Cbuh8HLA6WUt.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n145190-Cbuh8HLA6WUt.jpg"
                },
                "name": {
                  "first": "Martin",
                  "middle": null,
                  "last": "Halm",
                  "full": "Martin Halm",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 50053,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b50053-ZNrs5yuoht7Q.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b50053-ZNrs5yuoht7Q.png"
              },
              "name": {
                "first": "Captain",
                "middle": null,
                "last": null,
                "full": "Captain",
                "native": "本部長"
              }
            },
            "voiceActors": [
              {
                "id": 95459,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95459-guzIZZ731L9r.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95459-guzIZZ731L9r.png"
                },
                "name": {
                  "first": "Kinryuu",
                  "middle": null,
                  "last": "Arimoto",
                  "full": "Kinryuu Arimoto",
                  "native": "有本欽隆"
                }
              },
              {
                "id": 96648,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/1648.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/1648.jpg"
                },
                "name": {
                  "first": "Murphy",
                  "middle": null,
                  "last": "Dunne",
                  "full": "Murphy Dunne",
                  "native": null
                }
              },
              {
                "id": 139851,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n139851-WOg1hY8uKzAL.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n139851-WOg1hY8uKzAL.png"
                },
                "name": {
                  "first": "Michael",
                  "middle": null,
                  "last": "Schwarzmaier",
                  "full": "Michael Schwarzmaier",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 177262,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b177262-8MWW33trCr32.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b177262-8MWW33trCr32.png"
              },
              "name": {
                "first": "Lylie",
                "middle": null,
                "last": "Kisha",
                "full": "Lylie Kisha",
                "native": "ライリー記者"
              }
            },
            "voiceActors": [
              {
                "id": 96420,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/1420.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/1420.jpg"
                },
                "name": {
                  "first": "Kazusa",
                  "middle": null,
                  "last": "Murai",
                  "full": "Kazusa Murai",
                  "native": "村井かずさ"
                }
              },
              {
                "id": 95269,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95269-PhQ87wkVzLBb.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95269-PhQ87wkVzLBb.jpg"
                },
                "name": {
                  "first": "Mary Elizabeth",
                  "middle": null,
                  "last": "McGlynn",
                  "full": "Mary Elizabeth McGlynn",
                  "native": null
                }
              },
              {
                "id": 284729,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n284729-1dlN9TjFbu5s.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n284729-1dlN9TjFbu5s.jpg"
                },
                "name": {
                  "first": "Fabíola",
                  "middle": null,
                  "last": "Giardino",
                  "full": "Fabíola Giardino",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 240936,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b240936-pHHTccN4Qkug.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b240936-pHHTccN4Qkug.png"
              },
              "name": {
                "first": "Carlos",
                "middle": null,
                "last": null,
                "full": "Carlos",
                "native": "カルロス"
              }
            },
            "voiceActors": [
              {
                "id": 241104,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/default.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/default.jpg"
                },
                "name": {
                  "first": "Mário",
                  "middle": null,
                  "last": "Monjardim",
                  "full": "Mário Monjardim",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 4,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/4.jpg",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/4.jpg"
              },
              "name": {
                "first": "Ein",
                "middle": null,
                "last": null,
                "full": "Ein",
                "native": "アイン"
              }
            },
            "voiceActors": [
              {
                "id": 95011,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95011-2RfLzncNyvbR.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95011-2RfLzncNyvbR.png"
                },
                "name": {
                  "first": "Kouichi",
                  "middle": null,
                  "last": "Yamadera",
                  "full": "Kouichi Yamadera",
                  "native": "山寺宏一"
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 19118,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b19118-5xDFbLvjIeLp.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b19118-5xDFbLvjIeLp.png"
              },
              "name": {
                "first": "",
                "middle": null,
                "last": "Bull",
                "full": "Bull",
                "native": "ブル"
              }
            },
            "voiceActors": [
              {
                "id": 106923,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/11923.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/11923.jpg"
                },
                "name": {
                  "first": "Takehiro",
                  "middle": null,
                  "last": "Koyama",
                  "full": "Takehiro Koyama",
                  "native": "小山武宏"
                }
              },
              {
                "id": 95668,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/668.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/668.jpg"
                },
                "name": {
                  "first": "Michael",
                  "middle": null,
                  "last": "Gregory",
                  "full": "Michael Gregory",
                  "native": null
                }
              },
              {
                "id": 181526,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n181526-sAtD9ERftWrq.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n181526-sAtD9ERftWrq.png"
                },
                "name": {
                  "first": "Mario",
                  "middle": null,
                  "last": "Milita",
                  "full": "Mario Milita",
                  "native": null
                }
              },
              {
                "id": 106069,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n106069-ZK1eKreGxD1E.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n106069-ZK1eKreGxD1E.jpg"
                },
                "name": {
                  "first": "Domício",
                  "middle": null,
                  "last": "Costa",
                  "full": "Domício Costa",
                  "native": "Domício Costa dos Santos Filho"
                }
              },
              {
                "id": 156058,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/default.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/default.jpg"
                },
                "name": {
                  "first": "Thomas",
                  "middle": null,
                  "last": "Rau",
                  "full": "Thomas Rau",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 177261,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b177261-fcFXgS7thTuB.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b177261-fcFXgS7thTuB.png"
              },
              "name": {
                "first": "Old Lady",
                "middle": null,
                "last": null,
                "full": "Old Lady",
                "native": "オバさん"
              }
            },
            "voiceActors": [
              {
                "id": 95103,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95103-u0gfczhE9neg.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95103-u0gfczhE9neg.png"
                },
                "name": {
                  "first": "Kujira",
                  "middle": null,
                  "last": "",
                  "full": "Kujira",
                  "native": "くじら"
                }
              },
              {
                "id": 95536,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95536-jKbTvYfcrPaM.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95536-jKbTvYfcrPaM.jpg"
                },
                "name": {
                  "first": "Barbara",
                  "middle": null,
                  "last": "Goodson",
                  "full": "Barbara Goodson",
                  "native": null
                }
              },
              {
                "id": 145012,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n145012-fwn5JNDki5vQ.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n145012-fwn5JNDki5vQ.png"
                },
                "name": {
                  "first": "Ilona",
                  "middle": null,
                  "last": "Grandke",
                  "full": "Ilona Grandke",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "MAIN",
            "node": {
              "id": 2,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b2-0Iszg6Izgt4p.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b2-0Iszg6Izgt4p.png"
              },
              "name": {
                "first": "Faye",
                "middle": null,
                "last": "Valentine",
                "full": "Faye Valentine",
                "native": "フェイ・バレンタイン"
              }
            },
            "voiceActors": [
              {
                "id": 95014,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95014-tFYQYYhlVOF0.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95014-tFYQYYhlVOF0.png"
                },
                "name": {
                  "first": "Megumi",
                  "middle": null,
                  "last": "Hayashibara",
                  "full": "Megumi Hayashibara",
                  "native": "林原めぐみ"
                }
              },
              {
                "id": 95036,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95036-UkyIYVtEhoPk.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95036-UkyIYVtEhoPk.png"
                },
                "name": {
                  "first": "Wendee",
                  "middle": null,
                  "last": "Lee",
                  "full": "Wendee Lee",
                  "native": null
                }
              },
              {
                "id": 95763,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95763-Ci0xdLc6Z3a0.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95763-Ci0xdLc6Z3a0.jpg"
                },
                "name": {
                  "first": "Barbara",
                  "middle": null,
                  "last": "De Bortoli",
                  "full": "Barbara De Bortoli",
                  "native": null
                }
              },
              {
                "id": 95540,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/540.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/540.jpg"
                },
                "name": {
                  "first": "Miriam",
                  "middle": null,
                  "last": "Ficher",
                  "full": "Miriam Ficher",
                  "native": null
                }
              },
              {
                "id": 187678,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/default.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/default.jpg"
                },
                "name": {
                  "first": "Alexandra",
                  "middle": null,
                  "last": "Corréa",
                  "full": "Alexandra Corréa",
                  "native": null
                }
              },
              {
                "id": 156056,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/default.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/default.jpg"
                },
                "name": {
                  "first": "Marion",
                  "middle": null,
                  "last": "Sawatzki",
                  "full": "Marion Sawatzki",
                  "native": null
                }
              },
              {
                "id": 102809,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/7809.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/7809.jpg"
                },
                "name": {
                  "first": "Kriszta",
                  "middle": null,
                  "last": "Németh",
                  "full": "Kriszta Németh",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "MAIN",
            "node": {
              "id": 8435,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b8435-IL3RhRSazwFp.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b8435-IL3RhRSazwFp.png"
              },
              "name": {
                "first": "Electra",
                "middle": null,
                "last": "Ovilo",
                "full": "Electra Ovilo",
                "native": "エレクトラ・オヴィロゥ"
              }
            },
            "voiceActors": [
              {
                "id": 96019,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/1019.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/1019.jpg"
                },
                "name": {
                  "first": "Ai",
                  "middle": null,
                  "last": "Kobayashi",
                  "full": "Ai Kobayashi",
                  "native": "小林愛"
                }
              },
              {
                "id": 104918,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/9918.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/9918.jpg"
                },
                "name": {
                  "first": "Jennifer",
                  "middle": null,
                  "last": "Hale",
                  "full": "Jennifer Hale",
                  "native": null
                }
              },
              {
                "id": 109927,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/14927.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/14927.jpg"
                },
                "name": {
                  "first": "Seon Hye",
                  "middle": null,
                  "last": "Kim",
                  "full": "Seon Hye Kim",
                  "native": "선혜 김"
                }
              },
              {
                "id": 157374,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n157374-iq3dNBXijHRy.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n157374-iq3dNBXijHRy.jpg"
                },
                "name": {
                  "first": "Eleonora",
                  "middle": null,
                  "last": "De Angelis",
                  "full": "Eleonora De Angelis",
                  "native": null
                }
              },
              {
                "id": 180570,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n180570-1scQ9OoztdcN.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n180570-1scQ9OoztdcN.jpg"
                },
                "name": {
                  "first": "Carla",
                  "middle": null,
                  "last": "Pompílio",
                  "full": "Carla Pompílio",
                  "native": null
                }
              },
              {
                "id": 187676,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/default.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/default.jpg"
                },
                "name": {
                  "first": "Micheline",
                  "middle": null,
                  "last": "Goethals",
                  "full": "Micheline Goethals",
                  "native": null
                }
              },
              {
                "id": 156055,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/default.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/default.jpg"
                },
                "name": {
                  "first": "Elisabeth",
                  "middle": null,
                  "last": "Günther",
                  "full": "Elisabeth Günther",
                  "native": null
                }
              }
            ]
          },
          {
            "role": "SUPPORTING",
            "node": {
              "id": 177260,
              "image": {
                "large": "https://s4.anilist.co/file/anilistcdn/character/large/b177260-khoJ7k59LvKV.png",
                "medium": "https://s4.anilist.co/file/anilistcdn/character/medium/b177260-khoJ7k59LvKV.png"
              },
              "name": {
                "first": "Steve",
                "middle": null,
                "last": null,
                "full": "Steve",
                "native": "スティーブ"
              }
            },
            "voiceActors": [
              {
                "id": 95113,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n95113-z9eyYjTKSTX4.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n95113-z9eyYjTKSTX4.png"
                },
                "name": {
                  "first": "Rikiya",
                  "middle": null,
                  "last": "Koyama",
                  "full": "Rikiya Koyama",
                  "native": "小山力也"
                }
              },
              {
                "id": 95232,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/232.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/232.jpg"
                },
                "name": {
                  "first": "Kirk",
                  "middle": null,
                  "last": "Thornton",
                  "full": "Kirk Thornton",
                  "native": null
                }
              },
              {
                "id": 153004,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n153004-CgGmVAsohWRg.png",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n153004-CgGmVAsohWRg.png"
                },
                "name": {
                  "first": "Felipe",
                  "middle": null,
                  "last": "Grinnan",
                  "full": "Felipe Grinnan",
                  "native": null
                }
              },
              {
                "id": 132225,
                "image": {
                  "large": "https://s4.anilist.co/file/anilistcdn/staff/large/n132225-7d0lY86egZJs.jpg",
                  "medium": "https://s4.anilist.co/file/anilistcdn/staff/medium/n132225-7d0lY86egZJs.jpg"
                },
                "name": {
                  "first": "Ole",
                  "middle": null,
                  "last": "Pfennig",
                  "full": "Ole Pfennig",
                  "native": null
                }
              }
            ]
          }
        ]
      }
    }
  }
}    
"""
