package me.andannn.aniflow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
