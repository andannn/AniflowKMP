
plugins {
    id("kmp.library")
    id("compose.multiplatform.library")
}

android {
    namespace = "me.andannn.aniflow.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coil3.compose)
            implementation(libs.coil.network.ktor3)
        }

        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}
