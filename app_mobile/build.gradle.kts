import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.oss.licenses)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
    jvmToolchain(21)
}

android {
    namespace = "com.dertefter.etcetera"
    compileSdk = 37
    ndkVersion = "27.2.12479018"

    defaultConfig {
        applicationId = "com.dertefter.etcetera"
        minSdk = 29
        targetSdk = 37
        versionCode = project.property("appVersionCode").toString().toInt()
        versionName = project.property("appVersionName").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
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
}

dependencies {

    implementation(project(":common:data"))
    implementation(project(":common:navigation"))

    implementation(project(":app_mobile:design"))
    implementation(project(":app_mobile:feat:auth"))
    implementation(project(":app_mobile:feat:feed"))
    implementation(project(":app_mobile:feat:comments"))
    implementation(project(":app_mobile:feat:user"))
    implementation(project(":app_mobile:feat:new_post"))
    implementation(project(":app_mobile:feat:followers"))
    implementation(project(":app_mobile:feat:notifications"))
    implementation(project(":app_mobile:feat:banner_edit"))
    implementation(project(":app_mobile:feat:crash_reports"))
    implementation(project(":app_mobile:feat:post"))
    implementation(project(":app_mobile:feat:attachment_viewer"))
    implementation(project(":app_mobile:feat:search"))
    implementation(project(":app_mobile:feat:hashtag_feed"))
    implementation(project(":app_mobile:feat:switch_account"))
    implementation(project(":app_mobile:feat:settings"))
    implementation(project(":app_mobile:feat:settings_theme"))
    implementation(project(":app_mobile:feat:settings_account"))
    implementation(project(":app_mobile:feat:settings_security"))
    implementation(project(":app_mobile:feat:settings_privacy"))

    implementation(libs.core.splashscreen)
    implementation(libs.activity.compose)
    implementation(libs.appcompat)
    implementation(platform(libs.compose.bom.alpha))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.palette)
    runtimeOnly(libs.oss.licenses.droibit)
    implementation(libs.hilt.android)
    implementation(libs.play.services.wearable)
    ksp(libs.hilt.compiler)
    implementation(libs.navigation.compose)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material.kolor)
    implementation(libs.composefadingedges)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.haze)
    implementation(libs.haze.blur)
    implementation(libs.haze.blur.materials)
}
