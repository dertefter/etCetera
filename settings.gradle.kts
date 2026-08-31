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
                useModule("com.google.android.gms:oss-licenses-plugin:0.13.0")
            }
        }
    }

}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"

    id("com.autonomousapps.build-health") version "3.19.1"

    id("io.github.shivathapaa.aalekh") version "0.6.1"

    // Ensure shared classloader for analysis plugin
    id("com.android.application") version "9.3.2" apply false
    id("com.android.library") version "9.3.2" apply false
    id("org.jetbrains.kotlin.android") version "2.4.10" apply false
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



//common
include(":common:data")
include(":common:navigation")


//mobile
include(":app_mobile")

include(":app_mobile:design")

include(":app_mobile:feat:auth")
include(":app_mobile:feat:user")
include(":app_mobile:feat:feed")
include(":app_mobile:feat:comments")
include(":app_mobile:feat:new_post")
include(":app_mobile:feat:followers")
include(":app_mobile:feat:notifications")
include(":app_mobile:feat:banner_edit")
include(":app_mobile:feat:crash_reports")
include(":app_mobile:feat:post")
include(":app_mobile:feat:search")
include(":app_mobile:feat:hashtag_feed")
include(":app_mobile:feat:attachment_viewer")
include(":app_mobile:feat:switch_account")
include(":app_mobile:feat:settings")
include(":app_mobile:feat:settings_theme")
include(":app_mobile:feat:settings_account")
include(":app_mobile:feat:settings_security")


//wearable
include(":app_wearable")

include(":app_wearable:design")

include(":app_wearable:feat:feed")
include(":app_wearable:feat:user")
include(":app_wearable:feat:followers")
include(":app_wearable:feat:comments")
include(":app_wearable:feat:hashtag_feed")
include(":app_wearable:feat:post")
include(":app_wearable:feat:attachment_viewer")
