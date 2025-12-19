plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.serialization)
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.usecase.data.provider.impl"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:database"))
            implementation(project(":shared:domain:api"))
            api(project(":shared:usecase:data-provider:api"))
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
