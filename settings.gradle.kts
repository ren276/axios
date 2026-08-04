pluginManagement {
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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Kharcha"

include(":app")
include(":core:model")
include(":core:database")
include(":core:designsystem")
include(":feature:ingestion")
include(":feature:parser-rules")
include(":feature:llm")
include(":feature:transactions")
include(":feature:accounts")
include(":feature:budget")
include(":feature:reports")
include(":feature:settings")
include(":feature:onboarding")
