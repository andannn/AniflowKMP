plugins {
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
        namespace = "me.andannn.aniflow.usecase.operation"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
            implementation(project(":shared:platform"))
        }
    }
}
