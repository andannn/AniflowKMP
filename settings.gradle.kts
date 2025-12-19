rootProject.name = "AniFlowKMP"
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AniFlowKMP"
include(":androidApp")

// sharded modules
include(":shared:ios-export")

include(":shared:usecase:data-provider:api")
include(":shared:usecase:data-provider:impl")
include(":shared:usecase:paging")
include(":shared:usecase:sync")
include(":shared:usecase:operation")
include(":shared:usecase:util")

include(":shared:domain:api")
include(":shared:domain:impl-common")
include(":shared:domain:impl-data")

include(":shared:datastore")
include(":shared:network:service")
include(":shared:network:engine-mock")
include(":shared:network:common")
include(":shared:database")
include(":shared:platform")
