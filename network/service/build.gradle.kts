import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        val useMockHttpEngine =
            project.findProperty("USE_MOCK_HTTP_ENGINE")?.toString()?.toBoolean()
                ?: error("USE_MOCK_HTTP_ENGINE flag not found.")
        if (useMockHttpEngine) {
            val mockMain by creating {
                dependsOn(commonMain.get())
            }
            iosMain { dependsOn(mockMain) }
            androidMain { dependsOn(mockMain) }
            mockMain.dependencies {
                implementation(project(":network:engine-mock"))
            }
        } else {
            val realMain by creating {
                dependsOn(commonMain.get())
            }
            iosMain { dependsOn(realMain) }
            androidMain { dependsOn(realMain) }
            realMain.dependencies {
                implementation(project(":network:engine-real"))
            }
        }

        commonMain.dependencies {
            implementation(project(":network:common"))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        commonTest.dependencies {
            implementation(project(":network:engine-mock"))
        }
    }
}

android {
    namespace = "me.andannn.aniflow.service"
}