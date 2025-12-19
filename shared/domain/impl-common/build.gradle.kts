plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.domain.impl.common"
    }

    sourceSets.commonMain.dependencies {
        implementation(project(":shared:platform"))
        implementation(project(":shared:domain:api"))
        implementation(project(":shared:network:service"))
    }
}
