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

        androidUnitTest.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.sqldelight.jdbc.driver)
        }

        nativeMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}
