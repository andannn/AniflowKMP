plugins {
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.nativecoroutines)
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.usecase.data.provider"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            implementation(libs.kotlinx.serialization.core)
        }
    }
}
