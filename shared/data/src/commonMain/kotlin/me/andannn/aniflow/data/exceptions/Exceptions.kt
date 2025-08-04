package me.andannn.aniflow.data.exceptions

import me.andannn.aniflow.service.ServerException

open class DataException(
    override val message: String,
) : IllegalStateException(message)

class RemoteApiException(
    override val message: String,
) : DataException(message)

fun ServerException.toDataException(): DataException = RemoteApiException(message)
