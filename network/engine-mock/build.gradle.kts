plugins {
    id("kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.util)
        }
    }
}

android {
    namespace = "me.andannn.network.engine"
}