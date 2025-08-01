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
            export(project(":shared:data"))
            export(project(":shared:database"))
            export(project(":shared:network:common"))
            export(project(":shared:network:service"))
            export(libs.decompose)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.decompose)
            api(libs.essenty.lifecycle.coroutines)
            api(project(":shared:data"))
            api(project(":shared:database"))
            api(project(":shared:network:common"))
            api(project(":shared:network:service"))
        }
    }
}
