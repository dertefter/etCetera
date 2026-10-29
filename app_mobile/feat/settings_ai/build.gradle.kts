import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dertefter.settings_ai"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 25
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    api(project(":common:navigation"))
    implementation(project(":common:ai"))
    implementation(project(":app_mobile:design"))

    implementation(libs.activity.compose)
    api(libs.metro.runtime)
    api(libs.metrox.viewmodel)
    implementation(libs.metrox.viewmodel.compose)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation)
    api(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.compose.material3)
    api(libs.androidx.runtime)
    implementation(libs.compose.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    implementation(libs.compose.ui)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.kotlinx.coroutines.core)
}
