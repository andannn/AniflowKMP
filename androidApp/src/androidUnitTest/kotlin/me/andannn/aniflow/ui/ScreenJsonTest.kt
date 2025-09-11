package me.andannn.aniflow.ui

import kotlinx.serialization.json.Json
import me.andannn.aniflow.data.model.SettingItem
import me.andannn.aniflow.data.model.SettingOption
import me.andannn.aniflow.data.model.define.MediaCategory
import me.andannn.aniflow.data.model.define.UserTitleLanguage
import kotlin.test.Test

class ScreenJsonTest {
    @Test
    fun testScreenSerialization() {
        val screens =
            listOf(
                Screen.Home,
                Screen.MediaCategoryList(MediaCategory.CURRENT_SEASON_ANIME),
                Screen.Notification,
                Screen.Search,
                Screen.Settings,
                Screen.Dialog.Login,
                Screen.Dialog.SettingOption(
                    settingItem =
                        SettingItem.SingleSelect.build(
                            title = "Test Setting",
                            selectedOption = SettingOption.UserTitleLanguageOption(value = UserTitleLanguage.NATIVE),
                            buildOptions = {},
                        ),
                ),
            )

        for (screen in screens) {
            val json = Json.encodeToString(screen)
            println(json)
            val decodedScreen = Screen.fromJson(json)

            assert(screen == decodedScreen) {
                "Screen serialization/deserialization failed for $screen. JSON: $json, Decoded: $decodedScreen"
            }
        }
    }
}
