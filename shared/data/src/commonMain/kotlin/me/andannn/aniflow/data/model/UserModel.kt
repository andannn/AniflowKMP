package me.andannn.aniflow.data.model

data class UserModel(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val bannerImage: String? = null,
    val unreadNotificationCount: Int,
)
