plugins {
    id("kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.datastore)
            implementation(libs.datastore.preferences)
            implementation(libs.kotlinx.io.core)
        }
    }
}

android {
    namespace = "me.andannn.aniflow.core.datastore"
}
