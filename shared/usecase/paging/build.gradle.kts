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
        namespace = "me.andannn.aniflow.usecase.data.paging"
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":shared:domain:api"))
        }
    }
}
