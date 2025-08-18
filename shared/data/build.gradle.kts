import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.nativecoroutines)
    alias(libs.plugins.ksp)
}

android {
    namespace = "me.andannn.aniflow.data"
}

kotlin {
    compilerOptions {
        // https://kotlinlang.org/docs/whatsnew22.html#preview-of-context-parameters
        freeCompilerArgs.add("-Xcontext-parameters")
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

        androidUnitTest.dependencies {
            implementation(project(":shared:database"))
            implementation(project(":shared:datastore"))
            implementation(project(":shared:network:service"))
            implementation(project(":shared:network:engine-mock"))
            implementation(libs.datastore.preferences)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.sqldelight.jdbc.driver)
        }
    }
}
