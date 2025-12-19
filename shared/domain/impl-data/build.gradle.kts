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
        namespace = "me.andannn.aniflow.domain.impl"
    }

    sourceSets.commonMain.dependencies {
        api(project(":shared:domain:api"))
        implementation(project(":shared:domain:impl-common"))
        implementation(project(":shared:database"))
        implementation(project(":shared:datastore"))
        implementation(project(":shared:network:common"))
        implementation(project(":shared:network:service"))

        implementation(libs.kotlinx.datetime)
        implementation(libs.kotlinx.serialization.json)
    }
}
