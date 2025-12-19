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
        namespace = "me.andannn.aniflow.usecase.util"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":shared:platform"))
                implementation(project(":shared:domain:api"))
                implementation(project(":shared:domain:impl-data"))
                implementation(project(":shared:usecase:data-provider:impl"))
            }
        }
    }
}
