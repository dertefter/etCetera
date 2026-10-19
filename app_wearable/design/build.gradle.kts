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
    namespace = "com.dertefter.design"
    compileSdk { version = release(37) { minorApiLevel = 1 } }

    defaultConfig {
        minSdk = 30
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
    implementation(libs.appcompat)
    implementation(libs.androidx.palette)
    implementation(libs.kdroidfilter.composemediaplayer)
    implementation(platform(libs.compose.bom.alpha))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    api(libs.compose.wear.material3)
    api(libs.compose.wear.foundation)
    debugImplementation(libs.compose.ui.tooling)
    debugRuntimeOnly(libs.compose.ui.test.manifest)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    api(libs.androidx.media3.exoplayer)
    implementation(libs.haze)
    implementation(libs.zoomable)
    implementation(libs.androidx.annotation.experimental)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation)
    api(libs.androidx.foundation.layout)
    api(libs.androidx.foundation)
    implementation(libs.androidx.runtime.annotation)
    api(libs.androidx.runtime)
    implementation(libs.androidx.ui.geometry)
    api(libs.androidx.ui.text)
    api(libs.androidx.ui.unit)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.core)
    api(libs.androidx.graphics.shapes)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.database)
    implementation(libs.androidx.media3.datasource)
    androidTestRuntimeOnly(libs.core)
    androidTestRuntimeOnly(libs.androidx.runner)
    runtimeOnly(libs.androidx.transition)
    api(libs.material.color.utilities)
    implementation(libs.coil.compose.core)
    implementation(libs.coil.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.material.kolor.core)
}
