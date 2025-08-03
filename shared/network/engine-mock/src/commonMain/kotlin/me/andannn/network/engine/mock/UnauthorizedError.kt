/*
 * Copyright 2025, the AniflowKMP project contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package me.andannn.network.engine.mock

val UNAUTHORIZED_ERROR =
    """
    {
      "errors": [
        {
          "message": "Unauthorized.",
          "status": 401,
          "locations": [
            {
              "line": 2,
              "column": 5
            }
          ]
        }
      ],
      "data": {
        "UpdateUser": null
      }
    }
    """.trimIndent()
