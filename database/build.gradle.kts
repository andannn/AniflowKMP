plugins {
    id("kmp.library")
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "me.andannn.aniflow.database"
}

sqldelight {
    databases {
        create("AniflowDatabase") {
            generateAsync = true
            packageName.set("me.andannn.aniflow.database")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.coroutines.extensions)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }

        nativeMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}
