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
