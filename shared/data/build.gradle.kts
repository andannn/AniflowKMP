plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "me.andannn.aniflow.data"
}

kotlin {
    compilerOptions {
        // https://kotlinlang.org/docs/whatsnew22.html#preview-of-context-parameters
        freeCompilerArgs.add("-Xcontext-parameters")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":shared:database"))
                implementation(project(":shared:network:service"))
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }

        androidUnitTest.dependencies {
            implementation(project(":shared:database"))
            implementation(project(":shared:network:service"))
            implementation(project(":shared:network:engine-mock"))
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.sqldelight.jdbc.driver)
        }
    }
}
