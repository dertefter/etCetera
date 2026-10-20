import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.oss.licenses)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
    jvmToolchain(21)
}

android {
    namespace = "com.dertefter.etcetera"
    compileSdk { version = release(37) { minorApiLevel = 1 } }
    ndkVersion = "27.2.12479018"

    defaultConfig {
        applicationId = "com.dertefter.etcetera"
        minSdk = 25
        targetSdk = 37
        versionCode = project.property("appVersionCode").toString().toInt()
        versionName = project.property("appVersionName").toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
    implementation(project(":app_mobile:feat:settings_about"))
    implementation(project(":app_mobile:feat:report"))
    implementation(project(":app_mobile:feat:event_viewer"))
    implementation(project(":app_mobile:feat:onboarding"))

    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.coil.gif)
    implementation(libs.core.splashscreen)
    implementation(libs.activity.compose)
    implementation(libs.appcompat)
    runtimeOnly(libs.oss.licenses.droibit)
    implementation(libs.metro.runtime)
    implementation(libs.metrox.viewmodel)
    implementation(libs.metrox.viewmodel.compose)
    implementation(libs.play.services.wearable)
    implementation(libs.androidx.navigation.common)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.preview)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.compose.material3)
    implementation(libs.androidx.runtime.saveable)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.geometry)
    implementation(libs.compose.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    implementation(libs.compose.ui)
    implementation(libs.androidx.core)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.navigation3.runtime)
    implementation(libs.androidx.palette)
    implementation(libs.okhttp)
    implementation(libs.haze.blur.materials)
    implementation(libs.haze.blur)
    implementation(libs.haze)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.retrofit)
    implementation(libs.material.kolor.core)
    implementation(libs.core.ktx)
}
