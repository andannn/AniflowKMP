plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("kmp.ext")
    alias(libs.plugins.serialization)
//    alias(libs.plugins.apollo)
}

kmpExt {
    withAndroid()
    withIOS()
}

kotlin {
    androidLibrary {
        namespace = "me.andannn.aniflow.service"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:datastore"))
//            implementation(project(":shared:network:engine-mock"))
            implementation(project(":shared:network:common"))
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.encoding)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
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
