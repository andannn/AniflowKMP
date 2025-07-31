plugins {
    id("kmp.library")
}

android {
    namespace = "me.andannn.aniflow.data"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":database"))
                implementation(project(":network:service"))
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}
