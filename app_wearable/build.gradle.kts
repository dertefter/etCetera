import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

android {
    namespace = "com.dertefter.etcetera"
    compileSdk = 37
    ndkVersion = "27.2.12479018"

    defaultConfig {
        applicationId = "com.dertefter.etcetera"
        minSdk = 30
        targetSdk = 36
        versionCode = project.property("appVersionCode").toString().toInt() + 1
        versionName = project.property("appVersionName").toString()

    }


    signingConfigs {
        create("release") {
            val envKeystoreFile = System.getenv("ANDROID_KEYSTORE_FILE")
            val envKeystorePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
            val envKeyAlias = System.getenv("ANDROID_KEY_ALIAS")
            val envKeyPassword = System.getenv("ANDROID_KEY_PASSWORD")

            if (envKeystoreFile != null && envKeystorePassword != null && envKeyAlias != null && envKeyPassword != null) {
                storeFile = file(envKeystoreFile)
                storePassword = envKeystorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            } else {
                val debugConfig = signingConfigs.getByName("debug")
                storeFile = debugConfig.storeFile
                storePassword = debugConfig.storePassword
                keyAlias = debugConfig.keyAlias
                keyPassword = debugConfig.keyPassword
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
            ndk {
                debugSymbolLevel = "FULL"
            }
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            optimization {
                enable = true
            }
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":common:data"))
    implementation(project(":common:navigation"))

    implementation(project(":app_wearable:design"))

    implementation(project(":app_wearable:feat:feed"))
    implementation(project(":app_wearable:feat:user"))
    implementation(project(":app_wearable:feat:followers"))
    implementation(project(":app_wearable:feat:comments"))
    implementation(project(":app_wearable:feat:hashtag_feed"))
    implementation(project(":app_wearable:feat:post"))
    implementation(project(":app_wearable:feat:attachment_viewer"))

    implementation(libs.navigation.compose)
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.compose.wear.navigation3)
    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.activity.compose)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.wear.material3)
    implementation(libs.core.splashscreen)
    implementation(libs.play.services.wearable)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}