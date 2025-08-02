package me.andannn.aniflow.data.model

data class DataWithErrors<T>(
    val data: T? = null,
    val errors: List<Throwable> = emptyList(),
)
