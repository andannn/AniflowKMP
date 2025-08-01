plugins {
    id("kmp.library")
    alias(libs.plugins.serialization)
//    alias(libs.plugins.apollo)
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
                implementation(project(":shared:network:engine-real"))
            }
        }

        commonMain.dependencies {
            implementation(project(":shared:network:common"))
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.encoding)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        commonTest.dependencies {
            implementation(project(":shared:network:engine-mock"))
        }
    }
}

// apollo {
//    service("service") {
//        packageName.set("me.andannn.aniflow.service.generated")
//        generateApolloMetadata.set(false)
//        flattenModels.set(true)
//        generateFragmentImplementations.set(false)
//        generateAsInternal.set(true)
//
//        introspection {
//            endpointUrl.set("https://graphql.anilist.co")
//            schemaFile.set(file("src/commonMain/graphql/schema.graphqls"))
//        }
//    }
// }

android {
    namespace = "me.andannn.aniflow.service"
}
