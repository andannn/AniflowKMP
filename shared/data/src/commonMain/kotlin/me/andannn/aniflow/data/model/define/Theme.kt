package me.andannn.aniflow.data.model.define

enum class Theme(
    override val key: String,
) : StringKeyEnum {
    DARK("dark"),
    LIGHT("light"),
    SYSTEM("system"),
}
