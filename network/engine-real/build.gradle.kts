plugins {
    id("kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = "me.andannn.network.engine"
}