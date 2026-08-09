plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.dertefter.etcetera"
    compileSdk = 37

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
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.compose.wear.navigation)
    implementation(libs.compose.wear.navigation3)
    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.activity.compose)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.compose.wear.material3)
    implementation(libs.compose.wear.ui.tooling)
    implementation(libs.core.splashscreen)
    implementation(libs.play.services.wearable)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}