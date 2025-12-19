plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.nativecoroutines)
    alias(libs.plugins.serialization)
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.domain.api"
    }

    sourceSets.commonMain.dependencies {
        implementation(project(":shared:platform"))
        implementation(libs.kotlinx.serialization.core)
    }
}
