import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "me.andannn.aniflow.components"
}

kotlin {
    targets.withType<KotlinNativeTarget>().all {
        binaries.framework {
            baseName = "Shared"
            isStatic = true // or false, depending on your use case
            export(libs.decompose)
            export(libs.essenty.lifecycle)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.decompose)
            api(libs.essenty.lifecycle.coroutines)
            api(project(":shared:data"))
        }
    }
}
