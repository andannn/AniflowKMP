import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.serialization)
    alias(libs.plugins.nativecoroutines)
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.data"
    }

    // https://github.com/rickclephas/KMP-NativeCoroutines
    kotlin.sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }

    targets.withType<KotlinNativeTarget>().all {
        binaries.framework {
            baseName = "Shared"
            isStatic = true // or false, depending on your use case
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":shared:database"))
                implementation(project(":shared:datastore"))
                implementation(project(":shared:network:common"))
                implementation(project(":shared:network:service"))
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }
    }
}
