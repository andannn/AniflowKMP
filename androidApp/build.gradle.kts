import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.google.service)
    alias(libs.plugins.firebase.crashlytics)
    id("aniflow.android.lint")
}

android {
    namespace = "me.andannn.aniflow"

    defaultConfig {
        minSdk = 26
        targetSdk = 36
        compileSdk = 36

        applicationId = "me.andannn.aniflow"
        versionCode = (project.findProperty("VERSION_CODE") as? String?)?.toIntOrNull()
            ?: error("No version code found")
        versionName =
            project.findProperty("VERSION_NAME") as String? ?: error("No version name found")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "mode"

    productFlavors {
        create("presentation") {
            dimension = "mode"
            versionNameSuffix = "-presentation"

            val token: String = project.findProperty("presentation.token") as? String ?: ""
            buildConfigField("String", "PRESENTATION_TOKEN", "\"$token\"")
        }

        create("production") {
            dimension = "mode"

            buildConfigField("String", "PRESENTATION_TOKEN", "\"\"")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            val shouldSign = project.findProperty("android.releaseSigning") == "true"
            if (shouldSign) {
                signingConfigs {
                    create("release") {
                        storeFile = file(System.getenv("SIGNING_KEYSTORE_PATH"))
                        storePassword = System.getenv("KEYSTORE_PASSWORD")
                        keyAlias = System.getenv("KEY_ALIAS")
                        keyPassword = System.getenv("KEY_PASSWORD")
                    }
                }
                signingConfig = signingConfigs.getByName("release")
            } else {
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)

        // https://kotlinlang.org/docs/whatsnew22.html#preview-of-context-parameters
        freeCompilerArgs.add("-Xcontext-parameters")

        // https://kotlinlang.org/docs/whatsnew23.html#explicit-backing-fields
        freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

dependencies {
    implementation(project(":shared:domain:api"))
    implementation(project(":shared:usecase:data-provider:api"))
    implementation(project(":shared:usecase:paging"))
    implementation(project(":shared:usecase:operation"))
    implementation(project(":shared:usecase:sync"))
    implementation(project(":shared:platform"))
    implementation(project(":shared:usecase:util"))

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.kotlinx.datetime)

    implementation(project.dependencies.platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.ui.tooling)

    implementation(libs.napier)
    implementation(libs.coil3.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.navresult)
}

tasks.register("updateIosVersion") {
    group = "versioning"
    description = "Update iOS Info.plist with versionName and versionCode"

    val plistPath = project.rootDir.resolve("iosApp/iosApp/Info.plist")
    val versionCode =
        (project.findProperty("VERSION_CODE") as? String?)?.toIntOrNull()
            ?: error("No version code found")
    val versionName =
        project.findProperty("VERSION_NAME") as String? ?: error("No version name found")

    doLast {
        val plistFile = plistPath.readText()
        val updated =
            plistFile
                .replace(
                    Regex("<key>CFBundleShortVersionString</key>\\s*<string>.*?</string>"),
                    "<key>CFBundleShortVersionString</key>\n\t<string>$versionName</string>",
                ).replace(
                    Regex("<key>CFBundleVersion</key>\\s*<string>.*?</string>"),
                    "<key>CFBundleVersion</key>\n\t<string>$versionCode</string>",
                )

        plistPath.writeText(updated)
        println("✅ iOS Info.plist updated to version $versionName ($versionCode)")
    }
}
