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
include(":composeApp")

// sharded modules
include(":shared:network:service")
include(":shared:network:engine-mock")
include(":shared:network:engine-real")
include(":shared:network:common")
include(":shared:data")
include(":shared:database")

include(":ui")
