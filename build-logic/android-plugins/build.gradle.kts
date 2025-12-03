plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.licensee.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatformLibrary") {
            id = "kmp.library"
            implementationClass = "KMPLibraryConventionPlugin"
        }
        register("kotlinMultiplatformApplication") {
            id = "kmp.application"
            implementationClass = "KMPApplicationConventionPlugin"
        }
    }
}
