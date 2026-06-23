@file:Suppress("UnstableApiUsage", "UnstableApiUsage", "UnstableApiUsage", "UnstableApiUsage")
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

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.google.android.gms.oss-licenses-plugin") {
                useModule("com.google.android.gms:oss-licenses-plugin:0.12.0")
            }
        }
    }

}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "etCetera"

include(":app")

include(":core:data")
include(":core:design")
include(":core:navigation")

include(":feat:auth")
include(":feat:user")
include(":feat:feed")
include(":feat:comments")
include(":feat:new_post")
include(":feat:new_comment")
include(":feat:followers")
include(":feat:notifications")
include(":feat:banner_edit")
include(":feat:crash_reports")
include(":feat:post")
include(":feat:search")
include(":feat:hashtag_feed")
include(":feat:attachment_viewer")