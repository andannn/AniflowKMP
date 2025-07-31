plugins {
    id("kmp.library")
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
                implementation(project(":database"))
                implementation(project(":network:service"))
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }

        androidUnitTest.dependencies {
            implementation(project(":database"))
            implementation(project(":network:service"))
            implementation(project(":network:engine-mock"))
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.sqldelight.jdbc.driver)
        }
    }
}
