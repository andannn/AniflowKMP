plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "me.andannn.network.common"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
