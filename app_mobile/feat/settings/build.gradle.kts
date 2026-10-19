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
    namespace = "com.dertefter.settings"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 29
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
    implementation(project(":app_mobile:design"))

    api(libs.metro.runtime)
    api(libs.metrox.viewmodel)
    implementation(libs.metrox.viewmodel.compose)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.compose.material3)
    api(libs.androidx.runtime)
    implementation(libs.compose.ui.graphics)
    implementation(libs.androidx.ui.text)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.ui.unit)
    api(libs.compose.ui)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.viewmodel)
    implementation(libs.materialKolorMaterial3)
    implementation(libs.haze)
    implementation(libs.androidx.core)
}
