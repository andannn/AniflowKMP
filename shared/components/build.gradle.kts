plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "me.andannn.aniflow.components"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.decompose)
            implementation(libs.essenty.lifecycle.coroutines)
            implementation(project(":shared:data"))
        }
    }
}
