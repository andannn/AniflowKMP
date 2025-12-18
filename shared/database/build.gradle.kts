plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.sqldelight)
}

kmpExt {
    withAndroid()
    withIOS()
}

sqldelight {
    databases {
        create("AniflowDatabase") {
            dialect(libs.sqldelight.dialect.sqlite)
            version = 6
            generateAsync = true
            packageName.set("me.andannn.aniflow.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.database"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.coroutines.extensions)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }

        getByName("androidHostTest").dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.sqldelight.jdbc.driver)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

gradle.projectsEvaluated {
    val targets =
        listOf(
            ":shared:database:bundleLibCompileToJarDebug",
            ":shared:database:bundleLibCompileToJarRelease",
        )
    tasks.matching { it.path in targets }.configureEach {
        finalizedBy(tasks.named("generateCommonMainAniflowDatabaseSchema"))
    }
}
